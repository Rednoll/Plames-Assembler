package com.inwaiders.plames.assembler.domain.embodiments;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.inwaiders.plames.assembler.domain.CompileRequest;

@Entity(name = "Artifact")
@Table(name = "artifacts")
public class Artifact extends Embodiment {

	@Override
	public void load(CompileRequest request) throws Exception {
		
	}
	
	@Override
	public String getGradleDependencyLine() {
		
		return null;
	}
}
