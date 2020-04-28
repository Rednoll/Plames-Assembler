package enterprises.inwaiders.plames.assembler.dto.compile;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import enterprises.inwaiders.plames.assembler.dto.parts.PartBootloaderDto;
import enterprises.inwaiders.plames.assembler.dto.parts.PartCoreDto;
import enterprises.inwaiders.plames.assembler.dto.parts.PartModuleDto;
import enterprises.inwaiders.plames.eco.dto.DtoBase;

@JsonTypeInfo(defaultImpl = CompileRequestDto.class, use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property="@class")
public class CompileRequestDto extends DtoBase {
	
	public Long id = null;
	
	public PartBootloaderDto bootloader = null;
	public PartCoreDto core = null;
	public List<PartModuleDto> modules = new ArrayList<>();
}