package enterprises.inwaiders.plames.assembler.dao.embodiments;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import enterprises.inwaiders.plames.assembler.domain.embodiments.Embodiment;

@Repository
public interface EmbodimentRepository extends JpaRepository<Embodiment<?, ?>, Long>{

}
