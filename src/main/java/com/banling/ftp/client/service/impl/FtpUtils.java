package com.banling.ftp.client.service.impl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import sun.net.ftp.FtpClient;
import sun.net.ftp.FtpDirEntry;
import sun.net.ftp.FtpProtocolException;

import com.banling.ftp.client.module.FtpDownload;
import com.banling.ftp.client.module.FtpDownloadResult;

/**FTP工具类。<br>
 * 这里是通过JDK自带的工具包实现。<br>
 * 实现对Ftp服务器的操作：下载与上传文件。<br>
 * 不建议直接使用这个类的，因此方法设计为protected，使只能通过业务代理类IFtpService来调用。<br>
 * <br>
 * <b>如果系统中要对多个FTP服务器进行操作，就自己实例化FtpUtils对象，不是非要用Spring注解。</b>
 * @author Ban
 *
 */
@Service("ftpUtils")
public class FtpUtils extends IFtpUtils {
	
	private Logger logger = Logger.getLogger(getClass());
	
	//登录FTP服务器的基本信息
	@Value("${fileserver.ftp.ip}")
	private String ip=null;
	
	@Value("${fileserver.ftp.port}")
	private int port=21;
	
	@Value("${fileserver.ftp.userName}")
	private String userName=null;
	
	@Value("${fileserver.ftp.pw}")
	private String pw=null;
	
	@Override
	protected boolean uploadFile(Object ftpClient,String localDir,String localFile, String ftpDir) {
		// TODO Auto-generated method stub
		//使用缓冲
        BufferedOutputStream bo=null;
        BufferedInputStream bi=null;
        try {
        	FtpClient client=(FtpClient)ftpClient;
        	client.changeToParentDirectory();
        	client.changeDirectory(ftpDir);//ftpDir目录必须先存在。
	        
		    //将远程文件加入输出流中
		    bo = new BufferedOutputStream(client.putFileStream(localFile));
		    
		    //获取本地文件的输入流
		    File file= new File(localDir+localFile);
		    bi=new BufferedInputStream(new FileInputStream(file));
		    
		    //创建一个缓冲区
		    byte[] bytes = new byte[1024];
		    int c;
		    while ((c = bi.read(bytes)) != -1) {
		        bo.write(bytes, 0, c);
		    }
		    bo.flush();
		} catch (FtpProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				if(bi!=null){					
					bi.close();					
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				if(bo!=null){					
					bo.close();					
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
        
		return false;
	}

	@Override
	protected FtpDownloadResult downloadFile(Object ftpClient,String ftpDir, String ftpFile,String localDir) {
		// TODO Auto-generated method stub
		FtpDownloadResult result=null;
		try {
			FtpClient client=(FtpClient)ftpClient;
			result=this.downloadFile(client,ftpDir, ftpFile, localDir, null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
	@Override
	protected FtpDownloadResult downloadFile(Object ftpClient,String ftpDir,String ftpFile,String localDir,String localFile){
		// TODO Auto-generated method stub
		FtpDownloadResult result=new FtpDownloadResult(ftpDir,ftpFile,localDir);
		
		sun.net.ftp.FtpClient client=(sun.net.ftp.FtpClient)ftpClient;
		if(client!=null){
			//使用缓冲
			BufferedInputStream bi=null; 
			BufferedOutputStream bo=null;
			try {
				logger.info("当前的FTP工作目录："+client.getWorkingDirectory());
				//要处理文件不存在、读到是一个目录的情况
				bi=new BufferedInputStream(client.getFileStream(ftpDir+ftpFile));
				
				if(localDir!=null&&localDir.trim().length()>0&&(localDir.equals("./")==false||localDir.equals(".\\")==false)){
					File file=new File(localDir);
					if(file.exists()==false){
						file.mkdir();
					}					
				}
				//保存到本地时，是否改名
				if(localFile!=null&&localFile.trim().length()>0){
					bo = new BufferedOutputStream(new FileOutputStream(localDir+localFile));
				}else{
					bo = new BufferedOutputStream(new FileOutputStream(localDir+ftpFile));
				}				
				
				byte[] bytes = new byte[1024];
	            int c;
	            while ((c = bi.read(bytes)) != -1){
	            	bo.write(bytes, 0, c);
	                //os.write(bytes, 0, c);//写到本地目录中S
	                //System.out.println("打印字节数："+c);
	                //System.out.println(new String(bytes,"UTF-8"));
	            }
	            
	            bo.flush();
	            result.setResult(true);	            
			}catch (FtpProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				result.setResult(false);
				result.setNoFileExist(true);
				logger.error("文件"+result.getFtpfile()+"不存在。"+ e.getMessage());
			}catch(IOException e){
				e.printStackTrace();
				logger.error("文件"+result.getFtpfile()+"下载出错。"+ e.getMessage());
			}catch(Exception e){
				e.printStackTrace();
				logger.error("文件"+result.getFtpfile()+"下载出错。"+ e.getMessage());
			}finally{
				//关闭输出流
				try {
					if(bo!=null){
						bo.close();
					}					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				//关闭输入流
				try {
					if(bi!=null){
						bi.close();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}else{
			// TODO: 记录日志到log4j
			logger.info("当前没有FTP连接，请检查网络。");
		}
		
		// TODO: 根据result结果，同时记录日志到log4j 与 数据库表
		if(result.isResult()==true){
			logger.info("成功下载了FTP文件"+result.getFtpDir()+result.getFtpfile());
		}
		return result;
	}
	
	@Override
	protected List<FtpDownload> getAllFilsFromFtpDir(Object ftpClient,String ftpDir,String localDir) {
		// TODO Auto-generated method stub
		FtpClient client=(FtpClient)ftpClient;
		List<FtpDownload> toBeDownFiles=new ArrayList<FtpDownload>();
		String fileForInfo=null;
		try {
			Iterator<FtpDirEntry> listFiles = client.listFiles(ftpDir);
			int i=1;
			while(listFiles.hasNext()){
				String ftpFile=listFiles.next().getName();
				fileForInfo=ftpFile;
				logger.info("等待下载的FTP文件("+i+")："+ftpDir+ftpFile);
				i=i+1;
				
				//TODO: 记录日志到 log4j 与 数据库表中。
				
				FtpDownload ftpDownload=new FtpDownload(ftpDir,ftpFile,localDir);
				toBeDownFiles.add(ftpDownload);
			}
		} catch (FtpProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("下载"+ftpDir+fileForInfo+"出错。"+e.getMessage());
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("下载"+ftpDir+fileForInfo+"出错。"+e.getMessage());
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("下载"+ftpDir+fileForInfo+"出错。"+e.getMessage());
		}		
		return toBeDownFiles;
	}

	@Override
	public synchronized FtpClient createFtpClient() {
		// TODO Auto-generated method stub
		sun.net.ftp.FtpClient ftpClient=null;
		try {
			//检查IP的有效性
			if(ip!=null&&ip.trim().split(".").length==4){
				InetSocketAddress inetsocketaddress=new InetSocketAddress(ip,port);
				ftpClient=sun.net.ftp.impl.FtpClient.create(inetsocketaddress);//这里同时create执行了connect
				
				//是否要登录验证？
				if(userName!=null&&userName.trim().length()>0&&pw!=null&&pw.trim().length()>0){
					ftpClient.login(userName,pw.toCharArray());
				}				
				ftpClient.setBinaryType();//字节流
			}else{
				//IP无效，结束				
				//todo: 记录日志 到 log4j
				
				return null;
			}
		} catch(java.net.ConnectException e){
			e.printStackTrace();
			ftpClient=null;
			logger.error("创建FTP连接出错。"+e.getMessage());
		} catch (FtpProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			closeFtpClient(ftpClient);
			logger.error("创建FTP连接出错。"+e.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();	
			closeFtpClient(ftpClient);
			ftpClient=null;
			logger.error("创建FTP连接出错。"+e.getMessage());
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();	
			closeFtpClient(ftpClient);
			ftpClient=null;
			logger.error("创建FTP连接出错。"+e.getMessage());
		}
		if(ftpClient==null){
			//todo: 连接失败，同时记录日志到log4j 与数据库表中
			logger.error("创建FTP连接出错，请检查网络。");
		}
		
		return ftpClient;
	}
	
	@Override
	protected boolean closeFtpClient(Object ftpClient) {
		// TODO Auto-generated method stub
		if(ftpClient!=null){
			FtpClient client=null;
			try {
				client=(FtpClient)ftpClient;
				if(client.isConnected()){
					client.close();
					logger.error("成功关闭了一个FTP连接。");
				}
				client=null;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				client=null;
				logger.error("关闭FTP连接出错，请检查网络。");
			}catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				client=null;
				logger.error("关闭FTP连接出错，请检查网络。");
			}
			
		}
		
		
		return true;
	}
	
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPw() {
		return pw;
	}
	public void setPw(String pw) {
		this.pw = pw;
	}

	
	
}
