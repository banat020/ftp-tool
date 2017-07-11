package com.banling.ftp.client.module;

import java.io.Serializable;

/**数据结构，封装从FTP服务器下载数据的操作结果。
 * @author Ban
 *
 */
public final class FtpDownloadResult extends FtpDownload implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 9175026332497585280L;
	
	private boolean result=false;
	
	private boolean isNoTry=false;
	
	/**
	 * 是否不存在这个文件或者目录。
	 */
	private boolean isNoFileExist=false;
	
	public FtpDownloadResult(){
		
	}
	
	/**
	 * @param ftpDir String, Ftp服务器上的目录
	 * @param ftpfile String, ftpDir下的文件
	 * @param localDir String, 要保存到的本地目录
	 */
	public FtpDownloadResult(String ftpDir,String ftpfile,String localDir){
		super(ftpDir,ftpfile,localDir);
	}

	/**返回结果，仅当result==false时，要重试下载。
	 * @return boolean, 下载成功返回true，否则返回false。
	 */
	public boolean isResult() {
		return result;
	}

	/**返回结果，仅当result==false时，要重试下载。
	 * @param result boolean, 下载成功设置true，失败为false。
	 */
	public void setResult(boolean result) {
		this.result = result;
	}

	/**是否不存在这个文件或者目录。
	 * @return  boolean, 返回true，不存在文件或者目录；否则返回false
	 */
	public boolean isNoFileExist() {
		return isNoFileExist;
	}

	/**是否不存在这个文件或者目录。
	 * @param isNoFileExist boolean, 返回true，不存在文件或者目录；否则返回false
	 */
	public void setNoFileExist(boolean isNoFileExist) {
		this.isNoFileExist = isNoFileExist;
	}

	/**如果Ftp文件有问题，就不再尝试下载。
	 * @return boolean , true表示ftp文件有问题，否则false
	 */
	public boolean isNoTry() {
		return isNoTry;
	}

	/**如果Ftp文件有问题，就不再尝试下载。
	 * @param isNoTry  boolean , true表示ftp文件有问题，否则false
	 */
	public void setNoTry(boolean isNoTry) {
		this.isNoTry = isNoTry;
	}

}
