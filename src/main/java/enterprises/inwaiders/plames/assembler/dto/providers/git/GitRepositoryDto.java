package enterprises.inwaiders.plames.assembler.dto.providers.git;

import enterprises.inwaiders.plames.assembler.dto.providers.ProviderDtoBase;
import enterprises.inwaiders.plames.eco.dto.user.UserDto;

public class GitRepositoryDto extends ProviderDtoBase {
	
	public String address;
	public boolean isPublic;
	public UserDto owner;
}
