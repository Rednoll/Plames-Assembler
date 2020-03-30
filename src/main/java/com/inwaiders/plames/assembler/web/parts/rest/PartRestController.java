package com.inwaiders.plames.assembler.web.parts.rest;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;
import com.inwaiders.plames.assembler.dao.parts.PartBootloaderRepository;
import com.inwaiders.plames.assembler.dao.parts.PartCoreRepository;
import com.inwaiders.plames.assembler.dao.parts.PartModuleRepository;
import com.inwaiders.plames.assembler.domain.parts.Part;
import com.inwaiders.plames.assembler.domain.parts.PartBootloader;
import com.inwaiders.plames.assembler.domain.parts.PartCore;
import com.inwaiders.plames.assembler.domain.parts.PartModule;
import com.inwaiders.plames.assembler.dto.parts.PartDto;

@RestController
@RequestMapping("/rest/parts")
public class PartRestController {

	@Autowired
	private ObjectMapper mapper;
	
	@Autowired
	private PartBootloaderRepository bootloadersRep;
	
	@Autowired
	private PartCoreRepository coresRep;
	
	@Autowired
	private PartModuleRepository modulesRep;
	
	
	@PostConstruct
	private void postConstruct() {
		
		VisibilityChecker visChecker = mapper.getSerializationConfig().getDefaultVisibilityChecker();
			visChecker.withFieldVisibility(Visibility.ANY);
		
		mapper.setVisibility(visChecker);
	}
	
	@GetMapping("/{id}")
	public PartDto findById(@PathVariable Long id) {
		
		return Part.findById(id).toDto();
	}
	
	@GetMapping("/name/{name}")
	public PartDto findByName(@PathVariable String name) {
		
		return Part.findByName(name).toDto();
	}
	
	@GetMapping("/bootloaders")
	public List<PartBootloader> findBootloaders() {
		
		return bootloadersRep.findAll();
	}
	
	@GetMapping("/cores")
	public List<PartCore> findCores() {
		
		return coresRep.findAll();
	}
	
	@GetMapping("/modules")
	public List<PartModule> findModules(@RequestParam(required = false) String name) {
		
		if(name == null) {
			
			name = "";
		}
		
		return modulesRep.findByNameContainingIgnoreCaseOrderByName(name);
	}
	
	@GetMapping("")
	public List<PartDto> findAll() {
	
		List<PartDto> dtos = new ArrayList<>();
		List<Part> parts = Part.findAll();
		
		parts.forEach((Part part)-> dtos.add(part.toDto()));
		
		return dtos;
	}
}
