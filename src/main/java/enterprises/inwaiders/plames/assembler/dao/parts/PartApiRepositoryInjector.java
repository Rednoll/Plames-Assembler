package enterprises.inwaiders.plames.assembler.dao.parts;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import enterprises.inwaiders.plames.assembler.domain.parts.PartApi;

@Service
public class PartApiRepositoryInjector {

	@Autowired
	private PartApiRepository repository;
	
	@PostConstruct
	private void inject() {
		
		PartApi.setRepository(repository);
	}
}
