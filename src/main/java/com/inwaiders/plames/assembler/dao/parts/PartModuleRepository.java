package com.inwaiders.plames.assembler.dao.parts;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.inwaiders.plames.assembler.domain.parts.PartModule;

@Repository
public interface PartModuleRepository extends PartRepository<PartModule, Long> {

	public List<PartModule> findByNameContainingIgnoreCaseOrderByName(String name);
}
