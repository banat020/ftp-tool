package com.banling.ftp.client.service;

import com.banling.ftp.client.module.LoadDataResult;

/**(某种)业务数据全部下载到本地，上传到中间数据表中的业务就可以进行了。
 * @author Ban
 *
 */
public interface IFtpFileLoad2DB {
	
	/**解释已下载到本地的Ftp文件，并将数据保存到中间表中。
	 * @return LoadDataResult , 封装结果。
	 */
	LoadDataResult load2DB();
}
