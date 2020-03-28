package com.inwaiders.plames.assembler.dto.providers.git;

import com.inwaiders.plames.assembler.dto.providers.ProviderBaseDto;
import com.inwaiders.plames.eco.dto.user.UserDto;

public class GitRepositoryDto extends ProviderBaseDto {
	
	public String address;
	public boolean isPublic;
	public UserDto owner;
}
