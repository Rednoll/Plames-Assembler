package enterprises.inwaiders.plames.assembler.domain.build;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import enterprises.inwaiders.plames.assembler.domain.parts.PartApi;
import enterprises.inwaiders.plames.assembler.domain.parts.PartBootloader;
import enterprises.inwaiders.plames.assembler.domain.parts.PartCore;
import enterprises.inwaiders.plames.assembler.domain.parts.PartModule;

@Component
@Scope("singleton")
public class StepReinitHibernateProxies extends BuildStepBase {

	@Override
	public boolean run(BuildRequest request, Logger logger) throws Exception {
		
		request.api = PartApi.findById(request.api.getId());
		request.bootloader = PartBootloader.findById(request.bootloader.getId());
		request.core = PartCore.findById(request.core.getId());
		
		Set<PartModule> newModules = new HashSet<>();
		
			for(PartModule module : request.modules) {
				
				newModules.add(PartModule.findById(module.getId()));
			}

		request.modules = newModules;
		
		return true;
	}

	@Override
	public boolean isVisible() {
		
		return false;
	}

	@Override
	public String getName() {
		
		return "Reinit Hibernate proxies";
	}
}
