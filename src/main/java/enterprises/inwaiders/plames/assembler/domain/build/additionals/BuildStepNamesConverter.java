package enterprises.inwaiders.plames.assembler.domain.build.additionals;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import enterprises.inwaiders.plames.assembler.domain.build.BuildStep;

@Converter(autoApply = true)
public class BuildStepNamesConverter implements AttributeConverter<BuildStep, String> {

	@Override
	public String convertToDatabaseColumn(BuildStep attribute) {
		
		return attribute.getName();
	}

	@Override
	public BuildStep convertToEntityAttribute(String dbData) {
		
		return BuildStep.findByName(dbData);
	}
}
