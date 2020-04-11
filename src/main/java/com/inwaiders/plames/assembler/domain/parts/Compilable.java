package com.inwaiders.plames.assembler.domain.parts;

import com.inwaiders.plames.assembler.domain.build.BuildRequest;

public interface Compilable {

	public void prepareToCompile(BuildRequest request) throws Exception;
}
