package com.inwaiders.plames.assembler.domain.providers;

import java.io.File;

import com.inwaiders.plames.assembler.domain.build.BuildRequest;
import com.inwaiders.plames.assembler.dto.providers.ProviderDto;

public interface SrcProvider<DTO extends ProviderDto> extends Provider<DTO> {
	
	public void load(BuildRequest request, File destination) throws Exception;
}
