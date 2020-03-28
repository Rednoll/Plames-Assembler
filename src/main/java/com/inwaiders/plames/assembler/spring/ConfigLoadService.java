package com.inwaiders.plames.assembler.spring;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inwaiders.plames.PlamesAssembler;
import com.inwaiders.plames.assembler.MainConfig;

@Service
public class ConfigLoadService {

	@Autowired
	private MainConfig mainConfig;

	@PostConstruct
	private void load() {
		
		PlamesAssembler.CONFIG = mainConfig;
	}
}
