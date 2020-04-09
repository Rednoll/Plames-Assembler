package com.inwaiders.plames.assembler.domain.compile;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class CompileRequestProcessor {

	private static ExecutorService compileService = null;
	private static BlockingQueue<Runnable> tasks = null;
	
	public static int build(CompileRequest request) {
		
		compileService.submit(new CompileRequestTask(request));
		
		return getPlaceInQueue(request);
	}
	
	private static int getPlaceInQueue(CompileRequest request) {
		
		int i = 0;
		
		for(Runnable suspect : tasks) {
			
			if(((CompileRequestTask) suspect).request == request) return i;
			
			i++;
		}
		
		return i;
	}
	
	public static void initCompileService(int threadsCount) {
		
		tasks = new LinkedBlockingQueue<>();
		
		compileService = new ThreadPoolExecutor(threadsCount, threadsCount, 0, TimeUnit.MICROSECONDS, tasks);
	}
	
	private static class CompileRequestTask implements Runnable {
		
		private CompileRequest request = null;
		
		public CompileRequestTask(CompileRequest request) {
		
			this.request = request;
		}
		
		public void run() {
			
			try {
				
				request.build();
			}
			catch(Exception e) {
				
				e.printStackTrace();
			}
		}
		
		public CompileRequest getRequest() {
			
			return this.request;
		}
	}
}
