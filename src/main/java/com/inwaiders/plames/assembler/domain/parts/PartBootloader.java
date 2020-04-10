package com.inwaiders.plames.assembler.domain.parts;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.inwaiders.plames.assembler.dao.parts.PartBootloaderRepository;
import com.inwaiders.plames.assembler.dto.parts.PartBootloaderDto;

@Entity(name = "PartBootloader")
@Table(name = "part_bootloaders")
public class PartBootloader extends Part {

	private static PartBootloaderRepository repository;
	
	public void loadFromDto(PartBootloaderDto dto) {
		super.loadFromDto(dto);
	}
	
	public PartBootloaderDto toDto() {
		
		PartBootloaderDto dto = new PartBootloaderDto();
			toDto(dto);
			
		return dto;
	}
	
	public void toDto(PartBootloaderDto dto) {
		super.toDto(dto);
		
	}
	
	public static PartBootloader findById(Long id) {
		
		return repository.getOne(id);
	}
	
	public static PartBootloader create() {
		
		PartBootloader part = new PartBootloader();
		
		part = repository.save(part);
		
		return part;
	}
	
	public static void setRepository(PartBootloaderRepository rep) {
		
		repository = rep;
	}
}
