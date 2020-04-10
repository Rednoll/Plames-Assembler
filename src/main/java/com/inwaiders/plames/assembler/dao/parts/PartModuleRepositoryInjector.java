package com.inwaiders.plames.assembler.dao.parts;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inwaiders.plames.assembler.domain.parts.PartModule;

@Service
public class PartModuleRepositoryInjector {

	@Autowired
	private PartModuleRepository repository;

	@PostConstruct
	private void inject() {
		
		PartModule.setRepository(repository);
	}
}
