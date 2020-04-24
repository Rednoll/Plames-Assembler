package enterprises.inwaiders.plames.assembler.web.parts.rest;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import enterprises.inwaiders.plames.assembler.dao.parts.PartBootloaderRepository;
import enterprises.inwaiders.plames.assembler.dao.parts.PartCoreRepository;
import enterprises.inwaiders.plames.assembler.dao.parts.PartModuleRepository;
import enterprises.inwaiders.plames.assembler.domain.parts.Part;
import enterprises.inwaiders.plames.assembler.domain.parts.PartBootloader;
import enterprises.inwaiders.plames.assembler.domain.parts.PartCore;
import enterprises.inwaiders.plames.assembler.domain.parts.PartModule;
import enterprises.inwaiders.plames.assembler.dto.parts.PartBootloaderDto;
import enterprises.inwaiders.plames.assembler.dto.parts.PartCoreDto;
import enterprises.inwaiders.plames.assembler.dto.parts.PartDto;
import enterprises.inwaiders.plames.assembler.dto.parts.PartModuleDto;

@RestController
@RequestMapping("/rest/parts")
public class PartRestController {
	
	@Autowired
	private PartBootloaderRepository bootloadersRep;
	
	@Autowired
	private PartCoreRepository coresRep;
	
	@Autowired
	private PartModuleRepository modulesRep;
	
	@GetMapping("/{id}")
	public PartDto findById(@PathVariable Long id) {
		
		return Part.findById(id).toDto();
	}
	
	@GetMapping("/name/{name}")
	public PartDto findByName(@PathVariable String name) {
		
		return Part.findByName(name).toDto();
	}
	
	@GetMapping("/bootloaders")
	public List<PartBootloaderDto> findBootloaders() {
		
		List<PartBootloaderDto> dtos = new ArrayList<>();
		List<PartBootloader> bootloaders = bootloadersRep.findAll();
		
		for(PartBootloader bootloader : bootloaders) {
			
			dtos.add(bootloader.toDto());
		}
		
		return dtos;
	}
	
	@GetMapping("/cores")
	public List<PartCoreDto> findCores() {
		
		List<PartCoreDto> dtos = new ArrayList<>();
		List<PartCore> cores = coresRep.findAll();
		
		for(PartCore core : cores) {
			
			dtos.add(core.toDto());
		}
		
		return dtos;
	}
	
	@GetMapping("/modules")
	public List<PartModuleDto> findModules(@RequestParam(required = false) String name) {
		
		if(name == null) {
			
			name = "";
		}
		
		List<PartModule> modules = modulesRep.findByNameContainingIgnoreCaseOrderByName(name);
		
		List<PartModuleDto> dtos = new ArrayList<>();
		
		for(PartModule module : modules) {
			
			dtos.add(module.toDto());
		}
		
		return dtos;
	}
	
	@GetMapping("")
	public List<PartDto> findAll() {
	
		List<PartDto> dtos = new ArrayList<>();
		List<Part> parts = Part.findAll();
		
		for(Part part : parts) {
			
			dtos.add(part.toDto());
		}
		
		return dtos;
	}
}
