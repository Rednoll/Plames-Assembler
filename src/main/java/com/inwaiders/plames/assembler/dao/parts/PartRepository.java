package com.inwaiders.plames.assembler.dao.parts;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.inwaiders.plames.assembler.domain.parts.Part;

@Repository
public interface PartRepository<T extends Part, ID extends Long> extends JpaRepository<T, ID>{

	public T findByName(String name);
}
