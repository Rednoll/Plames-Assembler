package enterprises.inwaiders.plames.assembler.domain.parts;

import javax.persistence.Entity;
import javax.persistence.Table;

import enterprises.inwaiders.plames.assembler.dao.parts.PartBootloaderRepository;
import enterprises.inwaiders.plames.assembler.dao.parts.PartCoreRepository;
import enterprises.inwaiders.plames.assembler.dto.parts.PartCoreDto;

@Entity(name = "PartCore")
@Table(name = "part_cores")
public class PartCore extends Part {

	private static PartCoreRepository repository;
	
	public void loadFromDto(PartCoreDto dto) {
		super.loadFromDto(dto);
	}
	
	public PartCoreDto toDto() {
		
		PartCoreDto dto = new PartCoreDto();
			toDto(dto);
			
		return dto;
	}
	
	public void toDto(PartCoreDto dto) {
		super.toDto(dto);
		
	}
	
	public static PartCore findById(Long id) {
		
		return repository.getOne(id);
	}
	
	public static PartCore create() {
		
		PartCore part = new PartCore();
		
		part = repository.save(part);
		
		return part;
	}
	
	public static void setRepository(PartCoreRepository rep) {
		
		repository = rep;
	}
}
