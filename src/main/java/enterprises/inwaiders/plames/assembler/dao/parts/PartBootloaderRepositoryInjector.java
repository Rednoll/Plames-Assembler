package enterprises.inwaiders.plames.assembler.dao.parts;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import enterprises.inwaiders.plames.assembler.domain.parts.PartBootloader;

@Service
public class PartBootloaderRepositoryInjector {

	@Autowired
	private PartBootloaderRepository repository;
	
	@PostConstruct
	private void inject() {
		
		PartBootloader.setRepository(repository);
	}
}
