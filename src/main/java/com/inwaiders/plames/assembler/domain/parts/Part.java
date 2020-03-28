package com.inwaiders.plames.assembler.domain.parts;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.inwaiders.plames.assembler.dao.parts.PartsRepository;
import com.inwaiders.plames.assembler.domain.CompileRequest;
import com.inwaiders.plames.assembler.domain.embodiments.Embodiment;
import com.inwaiders.plames.assembler.domain.providers.ProviderBase;
import com.inwaiders.plames.eco.domain.user.User;

@Entity(name = "Part")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Part {

	protected static PartsRepository repository;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Long id;
	
	@Column(name = "name")
	private String name = null;
	
	@JoinColumn(name = "embodiment_id")
	@OneToOne(targetEntity = Embodiment.class)
	private Embodiment embodiment = null;
	
	@JoinColumn(name = "owner_id")
	@OneToOne(targetEntity = User.class)
	private User owner = null;
	
	public void load(CompileRequest request) throws Exception {
		
		embodiment.load(request);
	}
	
	public void prepareToCompile(CompileRequest request) {
		
		if(embodiment instanceof Compilable) {
			
			try {
				
				((Compilable) embodiment).prepareToCompile(request);
			}
			catch (Exception e) {
			
				e.printStackTrace();
			}
		}
	}

	public String getGradleDependencyLine() {
		
		return embodiment.getGradleDependencyLine();
	}

	public String getSettingsLine() {
		
		if(embodiment instanceof HasSettingsLine) {
			
			return ((HasSettingsLine) embodiment).getSettingsLine();
		}
		
		return null;
	}
	
	public void setEmbodiment(Embodiment emb) {
		
		this.embodiment = emb;
	}
	
	public Embodiment geEmbodiment() {
		
		return this.embodiment;
	}
	
	public void setOwner(User owner) {
		
		this.owner = owner;
	}
	
	public User getOwner() {
		
		return this.owner;
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
	
	public static Part findById(Long id) {
		
		return repository.getOne(id);
	}
	
	public static void setRepository(PartsRepository rep) {
		
		repository = rep;
	}
}
