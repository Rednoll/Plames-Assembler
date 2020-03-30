package com.inwaiders.plames.assembler.dao.parts;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inwaiders.plames.assembler.domain.parts.Part;

@Service
public class PartRepositoryInjector {

	@Autowired
	private PartRepository<Part, Long> repository;
	
	@PostConstruct
	public void inject() {
		
		Part.setRepository(repository);
	}
}
