package enterprises.inwaiders.plames.assembler.spring;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import enterprises.inwaiders.plames.PlamesAssembler;
import enterprises.inwaiders.plames.assembler.MainConfig;

@Service
public class ConfigLoadService {

	@Autowired
	private MainConfig mainConfig;

	@PostConstruct
	private void load() {
		
		PlamesAssembler.CONFIG = mainConfig;
	}
}
