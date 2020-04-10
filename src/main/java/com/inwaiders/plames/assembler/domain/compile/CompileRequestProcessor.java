package com.inwaiders.plames.assembler.domain.compile;

import java.io.File;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.LoggerFactory;

import com.inwaiders.plames.PlamesAssembler;

import ch.qos.logback.classic.Logger;

public class CompileRequestProcessor {

	private static ExecutorService compileService = null;
	private static BlockingQueue<Runnable> tasks = null;
	private static File rootDir = new File("./factory");
	private static File defaultProjectPattern = new File(rootDir, "/common-prototype");
	
	public static int build(CompileRequest request) {
		
		compileService.execute(new CompileRequestTask(request));
		
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
	
	public static CompileRequest createRequest() {
		
		CompileRequest request = PlamesAssembler.CONTEXT.getBean(CompileRequest.class);
			request.setProjectPattern(defaultProjectPattern);
		
			Logger logger = (Logger) LoggerFactory.getLogger("PlamesAssembler-"+request.getId());
				logger.setAdditive(false);
				
			request.setLogger(logger);
			
			request.setRootDir(new File(rootDir, "/request-"+request.getId()));
	
		return request;
	}
	
	private static class CompileRequestTask implements Runnable {
		
		private CompileRequest request = null;

		public CompileRequestTask(CompileRequest request) {
		
			this.request = request;
		}
		
		public void run() {
			
			try {
				
				CompileReport report = request.build();
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
