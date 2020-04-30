package enterprises.inwaiders.plames.assembler;

import java.io.File;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import enterprises.inwaiders.plames.assembler.domain.build.BuildRequest;
import enterprises.inwaiders.plames.assembler.domain.build.BuildRequestProcessor;

@Configuration
@PropertySource("file:main.properties")
public class MainConfig {
	
	@Value("${build.threads}")
	public int buildThreadsCount;

	@Value("${build.providers.async_loading}")
	public boolean providersAsyncLoading;
	
	@Value("${build.providers.loaders}")
	public int providersLoadersCount;
	
	@Value("${storage.path}")
	public String storagePath;
	
	@Value("${factory.path}")
	public String factoryPath;
	
	@PostConstruct
	private void post() {
		
		BuildRequest.providersLoadService = Executors.newFixedThreadPool(providersLoadersCount);
		BuildRequestProcessor.initCompileService(this.buildThreadsCount);
		
		BuildRequestProcessor.setRootDir(new File(factoryPath));
	}
}