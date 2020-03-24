package com.inwaiders.plames.assembler.domain.dependencies;

import com.inwaiders.plames.assembler.domain.CompileRequest;

public abstract class Dependency {

	public abstract void load(CompileRequest request) throws Exception;
	public abstract String getGradleDependencyLine();
}
