package enterprises.inwaiders.plames.assembler.dao.parts;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import enterprises.inwaiders.plames.assembler.domain.parts.Part;
import enterprises.inwaiders.plames.assembler.domain.parts.PartModule;

@Repository
public interface PartRepository<T extends Part, ID extends Long> extends JpaRepository<T, ID>{

	public List<T> findByNameContainingIgnoreCaseOrderByName(String name);
	
	public T findByName(String name);
}
