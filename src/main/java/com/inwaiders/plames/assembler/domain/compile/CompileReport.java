package com.inwaiders.plames.assembler.domain.compile;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.inwaiders.plames.assembler.dao.report.CompileReportRepository;
import com.inwaiders.plames.assembler.domain.parts.PartBootloader;
import com.inwaiders.plames.assembler.domain.parts.PartCore;
import com.inwaiders.plames.assembler.domain.parts.PartModule;

@Entity(name = "CompileReport")
@Table(name = "compile_reports")
public class CompileReport {

	private static CompileReportRepository repository;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Long id;
	
	@Column(name = "log", length = 10485760)
	private String log = null;
	
	@JoinColumn(name = "bootloader_id")
	@OneToOne(targetEntity = PartBootloader.class)
	private PartBootloader bootloader = null;
	
	@JoinColumn(name = "core_id")
	@OneToOne(targetEntity = PartCore.class)
	private PartCore core = null;
	
	@ManyToMany
	@JoinTable(name = "reports_modules_mtm", joinColumns = @JoinColumn(name = "report_id"), inverseJoinColumns = @JoinColumn(name = "module_id"))
	private Set<PartModule> modules = null;
	
	@Column(name = "gradle_build_pattern", length = 10485760)
	private String gradleBuildPattern = null;
	
	@Column(name = "gradle_settings_pattern", length = 10485760)
	private String gradleSettingsPattern = null;
	
	@Column(name = "result_status")
	@Enumerated(EnumType.STRING)
	private CompileRequest.Status resultStatus = null;
	
	public CompileReport() {
	
	}	
	
	public void setResultStatus(CompileRequest.Status status) {
		
		this.resultStatus = status;
	}
	
	public CompileRequest.Status getStatus() {
	
		return this.resultStatus;
	}
	
	public void setCore(PartCore core) {
		
		this.core = core;
	}
	
	public PartCore getCore() {
		
		return this.core;
	}
	
	public void setBootloader(PartBootloader bootloader) {
		
		this.bootloader = bootloader;
	}
	
	public PartBootloader getBootloader() {
		
		return this.bootloader;
	}
	
	public void setGradleBuildPattern(String pattern) {
		
		this.gradleBuildPattern = pattern;
	}
	
	public String getGradleBuildPattern() {
		
		return this.gradleBuildPattern;
	}
	
	public void setGradleSettingsPattern(String pattern) {
		
		this.gradleSettingsPattern = pattern;
	}
	
	public String getGradleSettingsPattern() {
		
		return this.gradleSettingsPattern;
	}
	
	public void setModules(Set<PartModule> modules) {
		
		this.modules = modules;
	}
	
	public void setLog(String log) {
		
		this.log = log;
	}
	
	public Long getId() {
		
		return this.id;
	}
	
	public void save() {
		
		repository.save(this);
	}
	
	public static CompileReport create() {
		
		CompileReport report = new CompileReport();
		
		report = repository.save(report);
		
		return report;
	}
	
	public static void setRepository(CompileReportRepository rep) {
		
		repository = rep;
	}
}
