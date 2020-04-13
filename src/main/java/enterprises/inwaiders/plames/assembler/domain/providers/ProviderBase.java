package enterprises.inwaiders.plames.assembler.domain.providers;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

import enterprises.inwaiders.plames.assembler.dao.providers.ProviderRepository;
import enterprises.inwaiders.plames.assembler.dto.providers.ProviderBaseDto;

@Entity(name = "ProviderBase")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class ProviderBase<DTO extends ProviderBaseDto> implements Provider<DTO> {
	
	protected static ProviderRepository repository;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Long id;
	
	public void loadFromDto(DTO dto) {
		
	}
	
	public abstract DTO toDto();
	
	public void toDto(ProviderBaseDto dto) {
		
		dto.id = this.id;
	}
	
	public Long getId() {
		
		return this.id;
	}
	
	public void save() {
		
		repository.save(this);
	}
	
	public static ProviderBase findById(Long id) {
		
		return repository.getOne(id);
	}
	
	public static void setRepository(ProviderRepository rep) {
		
		repository = rep;
	}
}
