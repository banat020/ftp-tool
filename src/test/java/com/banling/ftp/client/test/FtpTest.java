package com.banling.ftp.client.test;

import static org.junit.Assert.assertEquals;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.banling.ftp.client.service.IFtpService;

/**FTP原生实现方式的测试
 * @author Ban
 *
 */
public class FtpTest {

	private static BeanFactory beanFactory = null;
	private static IFtpService ftpService=null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		//取自test下的配置，与正式发布到Web环境的有差别。
		beanFactory = new ClassPathXmlApplicationContext(
			"config/applicationContext-fileserver-test.xml");
		ftpService=(IFtpService)beanFactory.getBean("ftpService");
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void testDowloadFtpFiles() {
		//如果成功，文件将会被下载到项目的根目录下
		boolean result=ftpService.downloadFiles();
		System.out.println("******************************");
		System.out.println("批量下载Ftp文件结果是："+result);
		assertEquals(true, result);		
	}
	
	@Test
	public void uploadFile(){
		ftpService.uploadFile("./", "test_created.txt", "", "test_created.txt");
	}

}
