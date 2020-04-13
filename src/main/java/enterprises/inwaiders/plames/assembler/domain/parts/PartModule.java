package enterprises.inwaiders.plames.assembler.domain.parts;

import javax.persistence.Entity;
import javax.persistence.Table;

import enterprises.inwaiders.plames.assembler.dao.parts.PartModuleRepository;
import enterprises.inwaiders.plames.assembler.dto.parts.PartModuleDto;

@Entity(name = "PartModule")
@Table(name = "part_modules")
public class PartModule extends Part {

	private static PartModuleRepository repository;
	
	public void loadFromDto(PartModuleDto dto) {
		super.loadFromDto(dto);
	}
	
	public PartModuleDto toDto() {
		
		PartModuleDto dto = new PartModuleDto();
			toDto(dto);
			
		return dto;
	}
	
	public void toDto(PartModuleDto dto) {
		super.toDto(dto);
		
	}
	
	public static PartModule findByName(String name) {
		
		return repository.findByName(name);
	}
	
	public static PartModule findById(Long id) {
		
		return repository.getOne(id);
	}
	
	public static PartModule create() {
		
		PartModule part = new PartModule();
		
		part = repository.save(part);
		
		return part;
	}
	
	public static void setRepository(PartModuleRepository rep) {
		
		repository = rep;
	}
}
