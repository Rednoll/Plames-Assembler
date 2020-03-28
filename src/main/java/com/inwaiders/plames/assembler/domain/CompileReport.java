package com.inwaiders.plames.assembler.domain;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import com.inwaiders.plames.assembler.dao.report.CompileReportRepository;
import com.inwaiders.plames.assembler.domain.parts.Part;

@Entity(name = "CompileReport")
@Table(name = "compile_reports")
public class CompileReport {

	private static CompileReportRepository repository;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Long id;
	
	@Column(name = "log", length = 10485760)
	private String log = null;
	
	@ManyToMany
	@JoinTable(name = "reports_parts_mtm", joinColumns = @JoinColumn(name = "report_id"), inverseJoinColumns = @JoinColumn(name = "part_id"))
	private Set<Part> parts = null;
	
	@Column(name = "gradle_build_pattern", length = 10485760)
	private String gradleBuildPattern = null;
	
	@Column(name = "gradle_settings_pattern", length = 10485760)
	private String gradleSettingsPattern = null;
	
	public CompileReport() {
	
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
	
	public void setParts(Set<Part> parts) {
		
		this.parts = parts;
	}
	
	public void setLog(String log) {
		
		this.log = log;
	}
	
	public Long getId() {
		
		return this.id;
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
