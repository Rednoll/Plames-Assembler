package com.inwaiders.plames.assembler.dto.compile;

import java.util.ArrayList;
import java.util.List;

import com.inwaiders.plames.assembler.dto.parts.PartBootloaderDto;
import com.inwaiders.plames.assembler.dto.parts.PartCoreDto;
import com.inwaiders.plames.assembler.dto.parts.PartModuleDto;

public class CompileRequestDto {
	
	public Long id = null;
	
	public PartBootloaderDto partBootloader = null;
	public PartCoreDto partCore = null;
	public List<PartModuleDto> modules = new ArrayList<>();
}