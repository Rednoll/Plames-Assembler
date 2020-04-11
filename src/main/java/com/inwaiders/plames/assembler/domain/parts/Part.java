package com.inwaiders.plames.assembler.domain.parts;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import com.inwaiders.plames.assembler.dao.parts.PartRepository;
import com.inwaiders.plames.assembler.domain.build.BuildRequest;
import com.inwaiders.plames.assembler.domain.embodiments.Embodiment;
import com.inwaiders.plames.assembler.dto.parts.PartDto;
import com.inwaiders.plames.eco.domain.user.User;

@Entity(name = "Part")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Part {

	private static PartRepository<Part, Long> repository;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Long id;
	
	@Column(name = "name")
	private String name = null;
	
	@Column(name = "description", length = 1024)
	private String description = null;
	
	@JoinColumn(name = "embodiment_id")
	@OneToOne(targetEntity = Embodiment.class)
	private Embodiment embodiment = null;
	
	@JoinColumn(name = "owner_id")
	@OneToOne(targetEntity = User.class)
	private User owner = null;
	
	@Column(name = "icon", length = 1024)
	private String icon = null;
	
	public void load(BuildRequest request) throws Exception {

		embodiment.load(request);
	}
	
	public void loadFromDto(PartDto dto) {
		
		this.name = dto.name;
		this.embodiment = Embodiment.findById(dto.embodiment.id);
		this.owner = User.findById(dto.owner.id);
		this.icon = dto.icon;
		this.description = dto.description;
	}
	
	public PartDto toDto() {
		
		PartDto dto = new PartDto();
			toDto(dto);
			
		return dto;
	}
	
	public void toDto(PartDto dto) {
		
		dto.id = this.getId();
		dto.name = this.getName();
		dto.owner = this.getOwner().toDto();
		dto.embodiment = this.getEmbodiment().toDto();
		dto.icon = this.getIcon();
		dto.description = this.getDescription();
	}
	
	public void prepareToCompile(BuildRequest request) {
		
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
	
	public void setIcon(String icon) {
		
		this.icon = icon;
	}
	
	public String getIcon() {
		
		return this.icon;
	}
	
	public void setEmbodiment(Embodiment emb) {
		
		this.embodiment = emb;
	}
	
	public Embodiment getEmbodiment() {
		
		return this.embodiment;
	}
	
	public void setOwner(User owner) {
		
		this.owner = owner;
	}
	
	public User getOwner() {
		
		return this.owner;
	}
	
	public void setName(String name) {
		
		this.name = name;
	}
	
	public String getName() {
	
		return this.name;
	}
	
	public void setDescription(String desc) {
		
		this.description = desc;
	}
	
	public String getDescription() {
		
		return this.description;
	}
	
	public Long getId() {
		
		return this.id;
	}
	
	public void save() {
		
		repository.save(this);
	}
	
	public static Part findByName(String name) {
		
		return repository.findByName(name);
	}
	
	public static Part findById(Long id) {
		
		return repository.getOne(id);
	}
	
	public static List<Part> findAll() {
	
		return repository.findAll();
	}
	
	public static void setRepository(PartRepository rep) {
		
		repository = rep;
	}
}
