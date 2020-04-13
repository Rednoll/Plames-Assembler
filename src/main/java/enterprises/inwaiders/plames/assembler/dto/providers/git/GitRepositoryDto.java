package enterprises.inwaiders.plames.assembler.dto.providers.git;

import enterprises.inwaiders.plames.assembler.dto.providers.ProviderBaseDto;
import enterprises.inwaiders.plames.eco.dto.user.UserDto;

public class GitRepositoryDto extends ProviderBaseDto {
	
	public String address;
	public boolean isPublic;
	public UserDto owner;
}
