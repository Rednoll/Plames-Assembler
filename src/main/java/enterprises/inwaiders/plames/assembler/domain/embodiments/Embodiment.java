package enterprises.inwaiders.plames.assembler.domain.embodiments;

import java.io.File;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import enterprises.inwaiders.plames.assembler.dao.embodiments.EmbodimentRepository;
import enterprises.inwaiders.plames.assembler.domain.build.BuildRequest;
import enterprises.inwaiders.plames.assembler.domain.providers.Provider;
import enterprises.inwaiders.plames.assembler.domain.providers.ProviderBase;
import enterprises.inwaiders.plames.assembler.dto.embodiments.EmbodimentDto;
import enterprises.inwaiders.plames.assembler.dto.providers.ProviderDtoBase;

@Entity(name = "Embodiment")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Embodiment<ProviderType extends Provider, DTO extends EmbodimentDto> {
	
	protected static EmbodimentRepository repository;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Long id;

	@Column(name = "name")
	protected String name = null;
	
	@JoinColumn(name = "provider_id")
	@OneToOne(targetEntity = ProviderBase.class)
	protected ProviderType provider;
	
	public abstract void load(BuildRequest request) throws Exception;
	
	public abstract File getJarFile(BuildRequest request);
	
	public abstract String getGradleDependencyLine();
	
	public void loadFromDto(DTO dto) {
		
		this.name = dto.name;
		this.provider = (ProviderType) ProviderBase.findById(((ProviderDtoBase) dto.provider).id);
	}
	
	public abstract DTO toDto();
	
	public void toDto(EmbodimentDto dto) {
		
		dto.id = this.getId();
		dto.provider = this.getProvider().toDto();
		dto.name = this.name;
	}
	
	public void setProvider(ProviderType provider) {
		
		this.provider = provider;
	}
	
	public ProviderType getProvider() {
		
		return this.provider;
	}
	
	public void setName(String name) {
		
		this.name = name;
	}
	
	public String getName() {
		
		return this.name;
	}
	
	public Long getId() {
		
		return this.id;
	}
	
	public void save() {
		
		repository.save(this);
	}
	
	public static Embodiment findById(Long id) {
		
		return repository.getOne(id);
	}
	
	public static void setRepository(EmbodimentRepository rep) {
		
		repository = rep;
	}
}
