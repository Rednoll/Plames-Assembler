package com.inwaiders.plames.assembler;

import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.inwaiders.plames.assembler.domain.compile.CompileRequest;
import com.inwaiders.plames.assembler.domain.compile.CompileRequestProcessor;

@Configuration
@PropertySource("file:main.properties")
public class MainConfig {
	
	@Value("${compile.threads}")
	public int compileThreadsCount;

	@Value("${compile.providers.async_loading}")
	public boolean providersAsyncLoading;
	
	@Value("${compile.providers.loaders}")
	public int providersLoadersCount;
	
	@PostConstruct
	private void post() {
		
		CompileRequest.providersLoadService = Executors.newFixedThreadPool(providersLoadersCount);
		CompileRequestProcessor.initCompileService(this.compileThreadsCount);
	}
}