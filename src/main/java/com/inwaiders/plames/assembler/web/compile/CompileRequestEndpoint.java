package com.inwaiders.plames.assembler.web.compile;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.inwaiders.plames.assembler.domain.compile.CompileRequest;
import com.inwaiders.plames.assembler.domain.compile.CompileRequestProcessor;
import com.inwaiders.plames.assembler.dto.compile.CompileRequestDto;
import com.inwaiders.plames.assembler.web.WebCompileLogAppender;

@Controller
@ResponseBody
@RequestMapping("/ajax/request")
@SessionAttributes("request")
public class CompileRequestEndpoint {
	
	@Autowired
	private ApplicationContext context;
	
	@PostMapping("/create")
	public void create(@RequestBody CompileRequestDto requestDto, HttpSession session) {
		
		CompileRequest request = context.getBean(CompileRequest.class);
		
		request.loadFromDto(requestDto);
	
		session.setAttribute("request", request);
	}
	
	@GetMapping("/build")
	public ResponseEntity<Integer> build(HttpSession session) {
		
		CompileRequest request = (CompileRequest) session.getAttribute("request");
		
		if(request == null) return ResponseEntity.notFound().build();
		
		int placeInQueue = CompileRequestProcessor.build(request);
	
		return new ResponseEntity<Integer>(placeInQueue, HttpStatus.OK);
	}

	@GetMapping("/status")
	public ResponseEntity<CompileRequest.Status> status(HttpSession session) {
		
		CompileRequest request = (CompileRequest) session.getAttribute("request");
		
		if(request == null) return ResponseEntity.notFound().build();
		
		return new ResponseEntity<CompileRequest.Status>(request.getStatus(), HttpStatus.OK);
	}
	
	@GetMapping("/console_log_news")
	public ResponseEntity<List<String>> consoleLog(HttpSession session) {
		
		CompileRequest request = (CompileRequest) session.getAttribute("request");
		
		if(request == null) return ResponseEntity.notFound().build();
		
		WebCompileLogAppender appender = (WebCompileLogAppender) request.getLogger().getAppender("web");
	
		return new ResponseEntity<List<String>>(appender.getLines(5), HttpStatus.OK);
	}
}
