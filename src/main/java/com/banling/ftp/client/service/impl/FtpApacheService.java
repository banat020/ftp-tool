package com.banling.ftp.client.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

import javax.annotation.Resource;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.banling.ftp.client.module.FtpDownload;
import com.banling.ftp.client.module.FtpDownloadResult;
import com.banling.ftp.client.service.IFtpService;

@Service("ftpApacheService")
public class FtpApacheService implements IFtpService {
	
	private Logger logger = Logger.getLogger(getClass());
	
	@Resource(name="ftpUtilsApache")
	private IFtpUtils ftpUtilsApache;
	
	@Value("${fileserver.ftp.ftpDir}")
	private String ftpDir;
	
	@Value("${fileserver.ftp.localDir}")
	private String localDir;
	
	@Value("${fileserver.ftp.ifClosePool}")
	private boolean ifClosePool=false;
	
	/**
	 * 定量线程池，容量为4个线程。
	 */
	private ExecutorService fixExecutorPool=Executors.newFixedThreadPool(4);
	
	/**
	 * 待下载的文件队列，不限容量
	 */
	private ConcurrentLinkedQueue<FtpDownload> downloadQueue=new ConcurrentLinkedQueue<FtpDownload>();
	
	/**
	 * FtpClient队列，是一个阻塞队列。<br>
	 * 因此，在取得FtpClient并使用完后，要记得放回队列中。
	 */
	private BlockingQueue<FTPClient> ftpClientQueue=new LinkedBlockingQueue<FTPClient>(4);
	
	private boolean initFtpClients() throws Exception{
		boolean result=true;
		for(int i=0;i<4;i++){				
		FTPClient client=(FTPClient)ftpUtilsApache.createFtpClient();//创建连接时，如果服务器不存在，那么会报连接超时；如是用户名或者密码错，连接时不会报错，只有在登录报错。
			ftpClientQueue.offer(client);
		}
		return result;
	}
	
	private boolean clossAllClients(){
		while(ftpClientQueue.isEmpty()==false){
			FTPClient client=ftpClientQueue.poll();
			ftpUtilsApache.closeFtpClient(client);
		}
		return true;
	}

	@Override
	public boolean downloadFiles() {
		// TODO Auto-generated method stub
		boolean result=false;
		try {
			initFtpClients();
			
			FTPClient ftpClient = ftpClientQueue.take();
			if (ftpClient == null) {
				// todo:连接到Ftp服务器失败的日志到数据库表中
				ftpClientQueue.put(ftpClient);//用完之后，必须回收连接
				return false;
			}
			
			if(putFtpFile2Queue4Dowload(ftpClient)==false){
				// todo:记录取Ftp文件列表失败的日志到数据库表中
				ftpClientQueue.put(ftpClient);//用完之后，必须回收连接
				return false;
			}else{
				ftpClientQueue.put(ftpClient);//用完之后，必须回收连接
			}
			
			result=downloadFilesUtil(2);//如果下载失败，就再重试一次
			if(ifClosePool){
				this.clossAllClients();
				this.fixExecutorPool.shutdown();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
	}
	
	/**将ftpDir目录下所有Ftp文件加入到队列Queue中等待下载。<br>
	 * <b>注意：只支持一级目录。</b>
	 */
	private boolean putFtpFile2Queue4Dowload(FTPClient ftpClient) {
		// TODO Auto-generated method stub	
		boolean result=true;
		List<FtpDownload> list=ftpUtilsApache.getAllFilsFromFtpDir(ftpClient,ftpDir,localDir);
		if(list==null||list.size()==0){
			logger.info(ftpDir+"目录下没有文件：请确认是否存这个目录，或者这个目录下是否有文件。");
			result=false;
		}else{
			for(FtpDownload dl:list){
				boolean getResult=downloadQueue.offer(dl);
				if(getResult==false){
					return false;//立即返回
				}
			}
		}
		return result;
	}
	
	/**批量下载FTP服务器上ftpDir目录下的所有文件，到本地目录localDir中。<br>
	 * 注意：这里的ftpDir是一级目录，不考虑子目录。<br>
	 * 通过多线程与线程池的方式下载。<br>
	 * <br>
	 * <b>注意：这个方法不是线程安全的（即不适合多个线程同时访问）。</b>
	 * <br>
	 * @param retryTimes int, 失败后尝试次数；当retryTimes<=1时，失败后不重试。
	 * @return boolean, <b>所有文件都下载成功</b>才能返回true，否则返回false。
	 * @throws InterruptedException,Exception
	 */
	private boolean downloadFilesUtil(int retryTimes) throws InterruptedException,Exception {
		// TODO Auto-generated method stub
		boolean result=true;
		result=excDownload();
		if(result==false&&retryTimes-1>0){
			downloadFilesUtil(retryTimes-1);
		}
		return result;
	}

	/**通过多线程方式批量下载
	 * 
	 * @return boolean, true表示批量下载成功，false表示批量下载失败
	 */
	private boolean excDownload(){
		boolean result=true;
		
		//加入到下载任务中
		List<Callable<FtpDownloadResult>> listTask=new ArrayList<Callable<FtpDownloadResult>>();
		while(downloadQueue.isEmpty()==false){
			FtpDownload ftpDl=downloadQueue.poll();//取出要下载的Ftp文件信息
			Callable<FtpDownloadResult> task=new DowLoadCallable(ftpDl);//构造任务，准备下载
			listTask.add(task);
		}
		
		if(listTask.size()==0){
			return false;
		}
		
		try {
			//批量执行任务
			List<Future<FtpDownloadResult>> listFuture=fixExecutorPool.invokeAll(listTask);
			for(Future<FtpDownloadResult> future:listFuture){
				FtpDownloadResult dlResult=future.get();
				if (dlResult.isResult() == false) {//处理下载失败
					if (dlResult.isNoFileExist()==false&&dlResult.isNoTry()==false) {// 重试下载，如果再失败就记录日志
						dlResult.setTryCount(dlResult.getTryCount()+1);
						downloadQueue.offer(dlResult);//加入到队列中重新下载
					} else {
						result = false;
						if(dlResult.isNoTry()){
							// todo:记录下载FTP文件失败的日志到log4j 与 数据库表中
						}
					}
				}
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
		
	@Override
	public boolean uploadFile(String ftpDir, String ftpFile, String localDir,
			String localFile) {
		// TODO Auto-generated method stub
		return false;
	}
	
/***************************************************************************************************************/	
	
	/**定义下载任务。
	 * <br>
	 * 内部类。
	 * @author Ban
	 *
	 */
	class DowLoadCallable implements Callable<FtpDownloadResult>{
		
		private FtpDownload ftpDl=null;
		
		public DowLoadCallable(FtpDownload ftpDl){
			this.ftpDl=ftpDl;
		}

		@Override
		public FtpDownloadResult call() throws Exception {
			// TODO Auto-generated method stub
			FTPClient ftpClient=ftpClientQueue.take();
			FtpDownloadResult result=ftpUtilsApache.downloadFile(ftpClient,ftpDl.getFtpDir(), ftpDl.getFtpfile(), ftpDl.getLocalDir());
			ftpClientQueue.put(ftpClient);//用完之后，回收连接
			return result;
		}
		
	}

}
