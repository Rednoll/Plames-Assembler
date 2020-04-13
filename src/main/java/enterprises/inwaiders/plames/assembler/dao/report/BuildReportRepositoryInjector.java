package enterprises.inwaiders.plames.assembler.dao.report;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import enterprises.inwaiders.plames.assembler.domain.build.BuildReport;

@Service
public class BuildReportRepositoryInjector {

	@Autowired
	private BuildReportRepository repository;

	@PostConstruct
	private void inject() {
		
		BuildReport.setRepository(repository);
	}
}
