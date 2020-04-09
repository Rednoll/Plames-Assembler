package com.inwaiders.plames.assembler.domain.parts;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.inwaiders.plames.assembler.dto.parts.PartModuleDto;

@Entity(name = "PartModule")
@Table(name = "part_modules")
public class PartModule extends Part {

	public void loadFromDto(PartModuleDto dto) {
		super.loadFromDto(dto);
	}
	
	public PartModuleDto toDto() {
		
		PartModuleDto dto = new PartModuleDto();
			toDto(dto);
			
		return dto;
	}
	
	public void toDto(PartModuleDto dto) {
		super.toDto(dto);
		
	}
	
	public static PartModule create() {
		
		PartModule part = new PartModule();
		
		part = repository.save(part);
		
		return part;
	}
}
