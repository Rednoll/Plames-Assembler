package com.inwaiders.plames.assembler.domain.embodiments;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import com.inwaiders.plames.assembler.dao.embodiments.EmbodimentRepository;
import com.inwaiders.plames.assembler.domain.CompileRequest;
import com.inwaiders.plames.assembler.domain.providers.Provider;
import com.inwaiders.plames.assembler.domain.providers.ProviderBase;

@Entity(name = "Embodiment")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Embodiment<ProviderType extends Provider> {
	
	protected static EmbodimentRepository repository;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Long id;
	
	@JoinColumn(name = "provider_id")
	@OneToOne(targetEntity = ProviderBase.class)
	protected ProviderType provider;
	
	public abstract void load(CompileRequest request) throws Exception;
	
	public abstract String getGradleDependencyLine();
	
	public void setProvider(ProviderType provider) {
		
		this.provider = provider;
	}
	
	public ProviderType getProvider() {
		
		return this.provider;
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
