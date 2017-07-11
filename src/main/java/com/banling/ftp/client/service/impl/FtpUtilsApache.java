package com.banling.ftp.client.service.impl;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.banling.ftp.client.module.FtpDownload;
import com.banling.ftp.client.module.FtpDownloadResult;

//Apache的实现方式
//与JDK的方式不兼容
@Service("ftpUtilsApache")
public class FtpUtilsApache extends IFtpUtils {

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
	protected boolean uploadFile(Object ftpClient, String localDir,
			String localFile, String ftpDir) {
		// TODO Auto-generated method stub
		//业务中不需要上传文件
		//因此没有实现
		return false;
	}

	@Override
	protected FtpDownloadResult downloadFile(Object ftpClient,
			String ftpDir, String ftpFile, String localDir) {
		// TODO Auto-generated method stub
		return downloadFile(ftpClient, ftpDir, ftpFile, localDir, null);
	}

	@Override
	protected FtpDownloadResult downloadFile(Object ftpClient,
			String ftpDir, String ftpFile, String localDir, String localFile) {
		// TODO Auto-generated method stub
		FtpDownloadResult result=new FtpDownloadResult();
		
		if(ftpClient!=null){
			String remoteFile=ftpDir+ftpFile;
			//使用缓冲
			BufferedOutputStream bo=null;
			try {
				File dir=new File(localDir);
				if(dir.exists()==false){
					dir.mkdir();
				}
				//是否改名
				if(localFile!=null&&localFile.length()>0){
					bo = new BufferedOutputStream(new FileOutputStream(localDir+localFile));
				}else{
					bo = new BufferedOutputStream(new FileOutputStream(localDir+ftpFile));
				}
				FTPClient client=(FTPClient)ftpClient;
				client.retrieveFile(remoteFile, bo);
				bo.flush();
				logger.info("下载了文件："+remoteFile);
				
				result.setFtpDir(ftpDir);
				result.setFtpfile(ftpFile);
				result.setLocalDir(localDir);
				result.setFtpfile(localFile);
				result.setResult(true);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				result.setNoFileExist(true);
				result.setNoTry(true);
				logger.error("文件"+remoteFile+"不存在");
			}catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				result.setNoTry(true);
			}finally{
				if(bo!=null){
					try {
						bo.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			
		}
		return result;
	}

	@Override
	protected List<FtpDownload> getAllFilsFromFtpDir(Object ftpClient,
			String ftpDir, String localDir) {
		// TODO Auto-generated method stub
		List<FtpDownload> list=null;
		if(ftpClient!=null){
			FTPClient client=(FTPClient)ftpClient;
			try {
				FTPFile[] listDir=client.listDirectories(ftpDir);
				Map<String,String> dirMap=new HashMap<String,String>();
				for(FTPFile dir:listDir){
					dirMap.put(dir.getName(), dir.getName());
				}
				FTPFile[] ftpFiles=client.listFiles(ftpDir);
				if(ftpFiles!=null&&ftpFiles.length>0){
					list=new ArrayList<FtpDownload>();
					for(int i=0,length=ftpFiles.length;i<length;i++){
						FtpDownload dw=new FtpDownload();
						String tempName=ftpFiles[i].getName();
						if(dirMap.containsKey(tempName)==false){
							dw.setFtpfile(tempName);
							dw.setFtpDir(ftpDir);
							dw.setLocalDir(localDir);
							list.add(dw);
							logger.info("等待下载的FTP文件("+(i+1)+")："+tempName);
						}else{//是一个目录
							logger.info(tempName+"是一个目录。");
						}					
					}
				}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return list;
	}

	@Override
	public FTPClient createFtpClient() {
		// TODO Auto-generated method stub
		FTPClient ftpClient=null;
		try {
			//检查IP的有效性
			if(ip!=null&&ip.trim().split(".").length==4){
				ftpClient=new FTPClient();
				ftpClient.connect(ip, port);
				ftpClient.login(userName, pw);
				int reply=ftpClient.getReplyCode();
				if(!FTPReply.isPositiveCompletion(reply)) {
					ftpClient.disconnect();
				}else{
					ftpClient.setKeepAlive(true);
				}
			}
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ftpClient;
	}

	@Override
	protected boolean closeFtpClient(Object ftpClient) {
		// TODO Auto-generated method stub		
		if(ftpClient!=null){
			FTPClient client=(FTPClient)ftpClient;
			if(client.isConnected()){
				try {
					client.noop();
					client.logout();
					client.disconnect();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}			
		}
		return true;
	}

}
