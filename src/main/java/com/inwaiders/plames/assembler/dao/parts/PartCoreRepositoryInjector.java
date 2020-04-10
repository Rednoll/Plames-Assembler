package com.inwaiders.plames.assembler.dao.parts;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inwaiders.plames.assembler.domain.parts.PartCore;

@Service
public class PartCoreRepositoryInjector {

	@Autowired
	private PartCoreRepository repository;

	@PostConstruct
	private void inject() {
		
		PartCore.setRepository(repository);
	}
}
