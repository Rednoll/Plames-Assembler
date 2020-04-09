package com.inwaiders.plames.assembler.dao.report;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inwaiders.plames.assembler.domain.compile.CompileReport;

@Service
public class CompileReportRepositoryInjector {

	@Autowired
	private CompileReportRepository repository;

	@PostConstruct
	private void inject() {
		
		CompileReport.setRepository(repository);
	}
}
