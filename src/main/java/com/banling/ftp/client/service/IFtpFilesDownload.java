package com.banling.ftp.client.service;

/**当某种业务的FTP数据文件准备好了之后，<br>
 * 就调用这个接口通知系统后面的下载业务可以进行了。<br>
 * @author Ban
 *
 */
public interface IFtpFilesDownload {
	/**将FTP文件下载到本地
	 * @return boolean
	 */
	boolean downLoadFiles();
}
