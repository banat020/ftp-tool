package com.banling.ftp.client.service;

/**FTP文件的下载与上传，业务实现。<br>
 * 是FtpUtils工具类的代理，用于实现功能业务。<br>
 * @author Ban
 *
 */
public interface IFtpService {
	
	/**批量下载FTP服务器上ftpDir目录下的所有文件，到本地目录localDir中。<br>
	 * 注意：这里的ftpDir是一级目录，不考虑子目录。<br>
	 * 通过多线程与线程池的方式下载。<br>
	 * <br>
	 * ftpDir与localDir，在实现类中注入
	 * @return boolean, <b>所有文件都下载成功</b>才能返回true，否则返回false。
	 */
	boolean downloadFiles();

	boolean uploadFile(String ftpDir,String ftpFile,String localDir,String localFile);
}
