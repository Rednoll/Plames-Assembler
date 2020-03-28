package com.inwaiders.plames.assembler.domain.parts;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity(name = "PartBootloader")
@Table(name = "part_bootloaders")
public class PartBootloader extends Part {

	public static PartBootloader create() {
		
		PartBootloader part = new PartBootloader();
		
		part = repository.save(part);
		
		return part;
	}
}
