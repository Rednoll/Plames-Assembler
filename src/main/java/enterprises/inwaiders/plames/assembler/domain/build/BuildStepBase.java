package enterprises.inwaiders.plames.assembler.domain.build;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public abstract class BuildStepBase implements BuildStep {

	
	public BaseDto toDto() {
		
		BaseDto dto = new BaseDto();
			dto.name = getName();
			dto.visible = isVisible();
			
		return dto;
	}
}
