package com.inwaiders.plames.assembler.domain.parts;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity(name = "PartModule")
@Table(name = "part_modules")
public class PartModule extends Part {

	public static PartModule create() {
		
		PartModule part = new PartModule();
		
		part = repository.save(part);
		
		return part;
	}
}
