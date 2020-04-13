package enterprises.inwaiders.plames.assembler.domain.providers;

import enterprises.inwaiders.plames.assembler.dto.providers.ProviderDto;

public interface Provider<DTO extends ProviderDto> {

	public void loadFromDto(DTO provider);
	public DTO toDto();
}
