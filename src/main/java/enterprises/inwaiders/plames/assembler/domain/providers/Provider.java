package enterprises.inwaiders.plames.assembler.domain.providers;

import enterprises.inwaiders.plames.assembler.dto.providers.ProviderDtoBase;

public interface Provider<DTO extends ProviderDtoBase> {

	public void loadFromDto(DTO provider);
	public DTO toDto();
}
