package com.inwaiders.plames.assembler.dao.parts;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.inwaiders.plames.assembler.domain.parts.Part;

@Repository
public interface PartsRepository extends JpaRepository<Part, Long>{

	public Part findByName(String name);
}
