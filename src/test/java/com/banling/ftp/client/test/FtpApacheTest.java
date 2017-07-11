package com.banling.ftp.client.test;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.banling.ftp.client.service.IFtpService;

/**FTP客户端通过Apache方式实现的测试
 * @author Ban
 *
 */
public class FtpApacheTest {

	private static BeanFactory beanFactory = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		//取自test下的配置，与正式发布到Web环境的有差别。
		beanFactory = new ClassPathXmlApplicationContext("config/applicationContext-fileserver.xml");
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void testApacheUtils() {
		IFtpService ftpService=(IFtpService)beanFactory.getBean("ftpApacheService");
		boolean result=ftpService.downloadFiles();
		System.out.println("******************************");
		System.out.println("批量下载Ftp文件结果是："+result);
	}

}
