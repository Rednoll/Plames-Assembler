package com.inwaiders.plames.assembler.dao.report;

import org.springframework.data.jpa.repository.JpaRepository;

import com.inwaiders.plames.assembler.domain.build.BuildReport;

public interface BuildReportRepository extends JpaRepository<BuildReport, Long>{

}
