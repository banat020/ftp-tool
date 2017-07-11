package com.banling.ftp.client.service.impl;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.banling.ftp.client.module.LoadDataResult;
import com.banling.ftp.client.service.IFtpFileLoad2DB;

public class FtpFileLoad2DB implements IFtpFileLoad2DB {

	private String loacalDir;
	
	private ExecutorService fixExecutorPool=Executors.newFixedThreadPool(4);
	
	@Override
	public LoadDataResult load2DB() {
		// TODO Auto-generated method stub
		LoadDataResult result=new LoadDataResult();
		
		//getLocalDirAllFile(),然后用多线程的方式解释文件并保存数据到中间表中。
		
		List<File> listFile=getLocalDirAllFile();
		Iterator<File> it=listFile.iterator();
		while(it.hasNext()){
			File file=it.next();
			
			Future f=fixExecutorPool.submit(new Task(file));
			

		}
		
		return result;
	}
	
	/**读取本地目标文件夹内的所有文件
	 * @return List<File>
	 */ 
	private List<File> getLocalDirAllFile(){
		
		return null;
	}
	
	
	/**解释文件，保存内存到数据库中。
	 * @author Ban
	 *
	 */
	class Task implements Callable{
		
		public Task(File file){
			
		}

		@Override
		public Object call() throws Exception {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
	

}
