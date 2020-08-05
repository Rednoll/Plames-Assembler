package enterprises.inwaiders.plames.assembler.domain.build;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import enterprises.inwaiders.plames.PlamesAssembler;
import enterprises.inwaiders.plames.assembler.domain.parts.PartModule;

@Component
@Scope("singleton")
public class StepLoad extends BuildStepBase {

	@Override
	public boolean run(BuildRequest request, Logger logger) throws Exception {
		
		request.api.load(request);
		request.bootloader.load(request);
		request.core.load(request);
		
		if(PlamesAssembler.CONFIG.providersAsyncLoading) {
			
			Set<Future<?>> asyncLoadersFutures = new HashSet<>();
			
			for(PartModule module : request.modules) {
				
				Future<?> future = request.providersLoadService.submit(()-> {
					
					try {
						
						module.load(request);
					}
					catch(Exception e) {
						
						e.printStackTrace();
					}
				});
				
				asyncLoadersFutures.add(future);
			}
			
			for(Future<?> future : asyncLoadersFutures) {
				
				future.get();
			}
		}
		else {
			
			for(PartModule module : request.modules) {
				
				module.load(request);
			}
		}
		
		return true;
	}

	@Override
	public boolean isVisible() {
		
		return true;
	}

	@Override
	public String getName() {
		
		return "Load";
	}
}
