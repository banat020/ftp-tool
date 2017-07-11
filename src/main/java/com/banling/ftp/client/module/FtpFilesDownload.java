package com.banling.ftp.client.module;

import com.banling.ftp.client.service.IFtpFilesDownload;
import com.banling.ftp.client.service.IFtpService;

public class FtpFilesDownload implements IFtpFilesDownload {
	
	private IFtpService FtpService;
	
	private String remoteDir;
	
	private String localDir;

	@Override
	public boolean downLoadFiles() {
		// TODO Auto-generated method stub
		//先设置remoteDir、localDir
		boolean result=FtpService.downloadFiles();
		return result;
	}
	
	

}
