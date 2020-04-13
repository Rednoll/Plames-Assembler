package enterprises.inwaiders.plames.assembler.dto.parts;

import enterprises.inwaiders.plames.assembler.dto.embodiments.EmbodimentDto;
import enterprises.inwaiders.plames.eco.dto.user.UserDto;

public class PartDto {

	public Long id = null;
	public String name = null;
	public UserDto owner = null;
	public EmbodimentDto embodiment = null;
	public String icon = null;
	public String description = null;
}
