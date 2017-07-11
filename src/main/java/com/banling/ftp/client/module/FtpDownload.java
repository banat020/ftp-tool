package com.banling.ftp.client.module;

import java.io.Serializable;

/**数据结构，封装等待下载的FTP文件
 * @author Ban
 *
 */
public class FtpDownload implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9181071348515977829L;
	
	//ftpDir、ftpfile、localDir，当result==false时要重试下载。
	private String ftpDir=null;//Ftp服务器上的目录
	private String ftpfile=null;//ftpDir下的文件
	private String localDir=null;//要保存到的本地目录
	
	private int tryCount=0; //已重试下载次数

	public FtpDownload(){
		
	}

	/**
	 * @param ftpDir String, Ftp服务器上的目录
	 * @param ftpfile String, ftpDir下的文件
	 * @param localDir String, 要保存到的本地目录
	 */
	public FtpDownload(String ftpDir,String ftpfile,String localDir){
		this.ftpDir=ftpDir;
		this.ftpfile=ftpfile;
		this.localDir=localDir;
	}
	
	/**Ftp服务器上的目录
	 * @return String, Ftp服务器上的目录
	 */
	public String getFtpDir() {
		return ftpDir;
	}

	/**Ftp服务器上的目录
	 * @param ftpDir String，Ftp服务器上的目录
	 */
	public void setFtpDir(String ftpDir) {
		this.ftpDir = ftpDir;
	}

	/**ftpDir下的文件
	 * @return String, ftpDir下的文件
	 */
	public String getFtpfile() {
		return ftpfile;
	}

	/**ftpDir下的文件
	 * @param ftpfile String, ftpDir下的文件
	 */
	public void setFtpfile(String ftpfile) {
		this.ftpfile = ftpfile;
	}

	/**要保存到的本地目录
	 * @return String， 要保存到的本地目录
	 */
	public String getLocalDir() {
		return localDir;
	}

	/**要保存到的本地目录
	 * @param localDir String， 要保存到的本地目录
	 */
	public void setLocalDir(String localDir) {
		this.localDir = localDir;
	}

	/**重试下载次数
	 * @return int
	 */
	public int getTryCount() {
		return tryCount;
	}

	/**重试下载次数
	 * @param cryCount int, 重试下载次数
	 */
	public void setTryCount(int tryCount) {
		this.tryCount = tryCount;
	}
}
