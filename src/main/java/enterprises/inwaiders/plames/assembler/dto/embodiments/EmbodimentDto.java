package enterprises.inwaiders.plames.assembler.dto.embodiments;

import enterprises.inwaiders.plames.assembler.dto.providers.ProviderDtoBase;
import enterprises.inwaiders.plames.eco.dto.DtoBase;

public class EmbodimentDto extends DtoBase {

	public Long id;
	public ProviderDtoBase provider;
	public String name;
}
