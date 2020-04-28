package enterprises.inwaiders.plames.assembler.domain.providers;

import java.io.File;

import enterprises.inwaiders.plames.assembler.domain.build.BuildRequest;
import enterprises.inwaiders.plames.assembler.dto.providers.ProviderDtoBase;

public interface SrcProvider<DTO extends ProviderDtoBase> extends Provider<DTO> {
	
	public void load(BuildRequest request, File destination) throws Exception;
}
