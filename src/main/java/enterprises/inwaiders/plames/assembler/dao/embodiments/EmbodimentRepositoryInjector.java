package enterprises.inwaiders.plames.assembler.dao.embodiments;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import enterprises.inwaiders.plames.assembler.domain.embodiments.Embodiment;

@Service
public class EmbodimentRepositoryInjector {

	@Autowired
	private EmbodimentRepository repository;

	@PostConstruct
	private void inject() {
		
		Embodiment.setRepository(repository);
	}
}
