package com.inwaiders.plames.assembler.spring;

import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inwaiders.plames.assembler.MainConfig;
import com.inwaiders.plames.assembler.PlamesAssembler;
import com.inwaiders.plames.assembler.domain.dependencies.ProjectDependency;

@Service
public class ConfigLoadService {

	@Autowired
	private MainConfig mainConfig;

	@PostConstruct
	private void load() {
		
		PlamesAssembler.CONFIG = mainConfig;
		
		ProjectDependency.loadService = Executors.newFixedThreadPool(PlamesAssembler.CONFIG.gitLoadersCount);
	}
}
