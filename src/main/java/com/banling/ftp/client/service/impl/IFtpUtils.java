package com.banling.ftp.client.service.impl;

import java.util.List;

import com.banling.ftp.client.module.FtpDownload;
import com.banling.ftp.client.module.FtpDownloadResult;

/**FTP工具类，提供工具方法。<br>
 * 实现对Ftp服务器的操作：下载与上传文件。<br>
 * <br><br>
 * <b>不建议直接使用这个类的</b>，因此方法设计为protected，使只能通过业务代理类IFtpService来调用；这样，就可以通过IFtpService的实现类实现多线程下载等。<br>
 * 因此IFtpUtils放在与IFtpService实现类的同一包中。<br>
 * <br>
 * @author Ban
 *
 */
public abstract class IFtpUtils {
	
	/**将一个本地文件localFile上传到FTP服务器的指定目录下。
	 * @param ftpClient FtpClient, Ftp连接
	 * @param localDir String, 本地目录，必须带目录分隔符“/”或者“\”；这分隔符根据不同的系统而定。
	 * @param localFile String, 本地文件
	 * @param ftpDir String, FTP服务器上的目录，必须带目录分隔符“/”或者“\”；这分隔符根据不同的系统而定。
	 * @return boolean, 成功返回true，否则返回false。
	 */
	protected abstract boolean uploadFile(Object ftpClient,String localDir,String localFile,String ftpDir);
	
	/**下载FTP服务器目录ftpDir下的一个文件ftpFile，到本地目录localDir中。
	 * @param ftpDir String, FTP服务器上的目录，要么为空，要么必须带目录分隔符“/”或者“\”；这分隔符根据不同的系统而定。
	 * @param ftpFile String, FTP服务器上的文件
	 * @param localDir String, 本地目录，要么为空，要么必须带目录分隔符“/”或者“\”；这分隔符根据不同的系统而定。
	 * @return FtpDownloadResult, 将FTP文件下载操作结果保存在一个数据体中返回。
	 */
	protected abstract FtpDownloadResult downloadFile(Object ftpClient,String ftpDir,String ftpFile,String localDir);
	
	/**下载FTP服务器目录ftpDir下的一个文件ftpFile，到本地目录localDir中，并命名为localFile。
	 * @param ftpClient FtpClient, FTP的连接
	 * @param ftpDir String, FTP服务器上的目录，要么为空，要么必须带目录分隔符“/”或者“\”；这分隔符根据不同的系统而定。
	 * @param ftpFile String, FTP服务器上的文件
	 * @param localDir String, 本地目录，要么为空，要么必须带目录分隔符“/”或者“\”；这分隔符根据不同的系统而定。
	 * @param localFile String, 保存为本地文件
	 * @return FtpDownloadResult, 将FTP文件下载操作结果保存在一个数据体中返回。
	 */
	protected abstract FtpDownloadResult downloadFile(Object ftpClient,String ftpDir,String ftpFile,String localDir,String localFile);
	
	/**取得FTP服务器上目录ftpDir下的所有文件名。<br>
	 * 注意，这里只考虑一级目录。<br>
	 * 如果是两级以上目录，可以利用这个工具再加上递归自行实现取得所有文件。
	 * @param ftpDir String, FTP服务器上的目录，要么为空，要么必须带目录分隔符“/”或者“\”；这分隔符根据不同的系统而定。
	 * @param localDir String, 本地目录，要么为空，要么必须带目录分隔符“/”或者“\”；这分隔符根据不同的系统而定。
	 * @return List<FtpDownload> 等待下载的FTP文件名列表
	 */
	protected abstract List<FtpDownload> getAllFilsFromFtpDir(Object ftpClient,String ftpDir,String localDir);
	
	/**创建FTP连接。<br>
	 * 可以用队列持有连接。
	 * @return FtpClient, FTP连接
	 */
	public abstract  Object  createFtpClient();
	
	/**关闭FTP连接
	 * @return boolean, 成功关闭返回true，否则返回false。
	 */
	protected abstract boolean closeFtpClient(Object ftpClient);
	
}
