package com.inwaiders.plames.assembler.domain.parts;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.inwaiders.plames.assembler.dto.parts.PartCoreDto;

@Entity(name = "PartCore")
@Table(name = "part_cores")
public class PartCore extends Part {

	public void loadFromDto(PartCoreDto dto) {
		super.loadFromDto(dto);
	}
	
	public PartCoreDto toDto() {
		
		PartCoreDto dto = new PartCoreDto();
			toDto(dto);
			
		return dto;
	}
	
	public void toDto(PartCoreDto dto) {
		super.toDto(dto);
		
	}
	
	public static PartCore create() {
		
		PartCore part = new PartCore();
		
		part = repository.save(part);
		
		return part;
	}
}
