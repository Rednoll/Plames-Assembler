package enterprises.inwaiders.plames.assembler.domain.parts;

import javax.persistence.Entity;
import javax.persistence.Table;

import enterprises.inwaiders.plames.assembler.dao.parts.PartApiRepository;
import enterprises.inwaiders.plames.assembler.dto.parts.PartCoreDto;

@Entity(name = "PartApi")
@Table(name = "part_apis")
public class PartApi extends Part {

	private static PartApiRepository repository;
	
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
	
	public static PartApi findById(Long id) {
		
		return repository.getOne(id);
	}
	
	public static PartApi create() {
		
		PartApi part = new PartApi();
		
		part = repository.save(part);
		
		return part;
	}
	
	public static void setRepository(PartApiRepository rep) {
		
		repository = rep;
	}
}