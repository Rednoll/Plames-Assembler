package enterprises.inwaiders.plames.assembler.dao.providers;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import enterprises.inwaiders.plames.assembler.domain.providers.ProviderBase;

@Service
public class ProviderRepositoryInjector {

	@Autowired
	private ProviderRepository repository;

	@PostConstruct
	private void inject() {
		
		ProviderBase.setRepository(repository);
	}
}
