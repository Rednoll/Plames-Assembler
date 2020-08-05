package enterprises.inwaiders.plames.assembler.domain.build;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;
import enterprises.inwaiders.plames.PlamesAssembler;
import enterprises.inwaiders.plames.eco.domain.user.User;

public class BuildRequestProcessor {

	private static ThreadPoolExecutor compileService = null;
	private static BlockingQueue<Runnable> tasks = null;
	private static File rootDir = new File("/factory");
	private static File defaultProjectPattern = new File(rootDir, "/common-prototype");
	private static BigDecimal totalBuildTime = new BigDecimal(0);
	private static long totalBuildsCount = 0;
	private static double averageBuildTime = 0;
	
	public static int build(BuildRequest request) {
		
		compileService.execute(new BuildRequestTask(request));
		
		return getPlaceInQueue(request);
	}
	
	public static void setRootDir(File dir) {
		
		rootDir = dir;
		defaultProjectPattern = new File(rootDir, "/common-prototype");
	}
	
	public static BuildRequestWaitData getRequestWaitData(BuildRequest request) {
		
		int placeInQueue = getPlaceInQueue(request);
		double estimatedWaitingTime = averageBuildTime * placeInQueue / (double)compileService.getActiveCount() / 1000D;
		
		return new BuildRequestWaitData(placeInQueue, estimatedWaitingTime);
	}
	
	public static int getPlaceInQueue(BuildRequest request) {
		
		int i = 0;
		
		for(Runnable suspect : tasks) {
		
			i++;
			
			if(((BuildRequestTask) suspect).request == request) return i;
		}
		
		return i;
	}
	
	public static void initCompileService(int threadsCount) {
		
		tasks = new LinkedBlockingQueue<>();
		
		compileService = new ThreadPoolExecutor(threadsCount, threadsCount, 0, TimeUnit.MICROSECONDS, tasks);
	}
	
	public static BuildRequest createRequest(User owner) {
		
		BuildRequest request = PlamesAssembler.CONTEXT.getBean(BuildRequest.class);
			request.setProjectPattern(defaultProjectPattern);
			request.setOwner(owner);
			
			Logger logger = (Logger) LoggerFactory.getLogger("PlamesAssembler-"+request.getId());
				logger.setAdditive(false);
				
			request.setLogger(logger);
			
			request.setRootDir(new File(rootDir, "/request-"+request.getId()));
	
		return request;
	}
	
	private static class BuildRequestTask implements Runnable {
		
		private BuildRequest request = null;

		public BuildRequestTask(BuildRequest request) {
		
			this.request = request;
		}
		
		public void run() {
			
			try {
				
				long start = System.currentTimeMillis();
				
				BuildReport report = request.build();
				
				long end = System.currentTimeMillis();
				
				synchronized(totalBuildTime) {
				
					totalBuildsCount++;
					totalBuildTime = totalBuildTime.add(new BigDecimal(String.valueOf(end-start))); //TODO: opti
					
					averageBuildTime += totalBuildTime.divide(new BigDecimal(String.valueOf(totalBuildsCount)), RoundingMode.CEILING).doubleValue();
				}
			}
			catch(Exception e) {
				
				e.printStackTrace();
			}
		}

		public BuildRequest getRequest() {
			
			return this.request;
		}
	}
}
