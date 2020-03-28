package com.inwaiders.plames.assembler.domain.parts;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity(name = "PartCore")
@Table(name = "part_cores")
public class PartCore extends Part {

	public static PartCore create() {
		
		PartCore part = new PartCore();
		
		part = repository.save(part);
		
		return part;
	}
}
