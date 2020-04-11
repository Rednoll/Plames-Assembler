package com.inwaiders.plames.assembler;

import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.inwaiders.plames.assembler.domain.build.BuildRequest;
import com.inwaiders.plames.assembler.domain.build.BuildRequestProcessor;

@Configuration
@PropertySource("file:main.properties")
public class MainConfig {
	
	@Value("${build.threads}")
	public int buildThreadsCount;

	@Value("${build.providers.async_loading}")
	public boolean providersAsyncLoading;
	
	@Value("${build.providers.loaders}")
	public int providersLoadersCount;
	
	@PostConstruct
	private void post() {
		
		BuildRequest.providersLoadService = Executors.newFixedThreadPool(providersLoadersCount);
		BuildRequestProcessor.initCompileService(this.buildThreadsCount);
	}
}