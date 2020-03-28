package com.inwaiders.plames.assembler.domain.providers;

import java.io.File;

import com.inwaiders.plames.assembler.domain.CompileRequest;

public interface SrcProvider extends Provider {
	
	public void load(CompileRequest request, File destination) throws Exception;
}
