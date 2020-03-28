package com.inwaiders.plames.assembler.dao.providers;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.inwaiders.plames.assembler.domain.providers.ProviderBase;

@Repository
public interface ProviderRepository extends JpaRepository<ProviderBase, Long>{

}
