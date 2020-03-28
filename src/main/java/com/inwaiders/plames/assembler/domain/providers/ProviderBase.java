package com.inwaiders.plames.assembler.domain.providers;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

import com.inwaiders.plames.assembler.dao.providers.ProviderRepository;

@Entity(name = "ProviderBase")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class ProviderBase implements Provider {
	
	protected static ProviderRepository repository;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Long id;
	
	public Long getId() {
		
		return this.id;
	}
	
	public void save() {
		
		repository.save(this);
	}
	
	public ProviderBase findById(Long id) {
		
		return repository.getOne(id);
	}
	
	public static void setRepository(ProviderRepository rep) {
		
		repository = rep;
	}
}
