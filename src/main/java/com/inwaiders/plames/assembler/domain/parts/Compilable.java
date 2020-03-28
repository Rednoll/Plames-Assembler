package com.inwaiders.plames.assembler.domain.parts;

import com.inwaiders.plames.assembler.domain.CompileRequest;

public interface Compilable {

	public void prepareToCompile(CompileRequest request) throws Exception;
}
