package com.inwaiders.plames.assembler.web.parts.rest;

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
import com.inwaiders.plames.assembler.domain.parts.Part;
import com.inwaiders.plames.assembler.dto.PartDto;

@RestController
@RequestMapping("/rest/parts")
public class PartRestController {

	@Autowired
	private ObjectMapper mapper;
	
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
	
	@GetMapping("")
	public PartDto findByName(@RequestParam("name") String name) {
		
		return Part.findByName(name).toDto();
	}
}
