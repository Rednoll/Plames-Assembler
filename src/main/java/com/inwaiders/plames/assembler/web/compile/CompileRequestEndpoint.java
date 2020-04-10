package com.inwaiders.plames.assembler.web.compile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.context.request.async.DeferredResult;

import com.inwaiders.plames.assembler.domain.compile.CompileRequest;
import com.inwaiders.plames.assembler.domain.compile.CompileRequestProcessor;
import com.inwaiders.plames.assembler.dto.compile.CompileRequestDto;
import com.inwaiders.plames.assembler.web.WebCompileLogAppender;

@Controller
@ResponseBody
@RequestMapping("/ajax/request")
@SessionAttributes("request")
public class CompileRequestEndpoint {
	
	private static Map<String, CompileRequest> requests = new HashMap<>();
	
	@PostMapping("/create")
	public ResponseEntity<Boolean> create(@RequestBody CompileRequestDto requestDto, HttpSession session) {
		
		String sessionId = session.getId();
		
		if(requests.get(sessionId) != null) {
			
			CompileRequest oldReqeust = requests.get(sessionId);
			
			if(oldReqeust.isBuilded()) {
				
				requests.remove(sessionId);
			}
			else {
				
				return ResponseEntity.status(HttpStatus.CONFLICT).body(false);
			}
		}
		
		CompileRequest request = CompileRequestProcessor.createRequest();
		
		request.loadFromDto(requestDto);
		
		requests.put(sessionId, request);

		return ResponseEntity.ok().body(true);
	}
	
	@GetMapping("/build")
	public ResponseEntity<Integer> build(HttpSession session) {
		
		CompileRequest request = requests.get(session.getId());
		
		if(request == null) return ResponseEntity.notFound().build();
		
		int placeInQueue = CompileRequestProcessor.build(request);
	
		return new ResponseEntity<Integer>(placeInQueue, HttpStatus.OK);
	}
	
	@GetMapping("/wait")
	public DeferredResult<ResponseEntity<Boolean>> waitComplete(HttpSession session) {
	
		CompileRequest request = requests.get(session.getId());
		
		DeferredResult<ResponseEntity<Boolean>> result = new DeferredResult<ResponseEntity<Boolean>>();
		
		if(request == null) {
			
			result.setResult(ResponseEntity.notFound().build());
			return result;
		}
	
		return result;
	}
	
	@GetMapping("/status")
	public ResponseEntity<CompileRequest.Status> status(HttpSession session) {
		
		CompileRequest request = requests.get(session.getId());
		
		if(request == null) return ResponseEntity.notFound().build();
		
		return new ResponseEntity<CompileRequest.Status>(request.getStatus(), HttpStatus.OK);
	}
	
	@GetMapping("/compile_log_news")
	public ResponseEntity<List<String>> compileLog(HttpSession session) {
		
		CompileRequest request = requests.get(session.getId());

		if(request == null) return ResponseEntity.notFound().build();
		
		WebCompileLogAppender appender = (WebCompileLogAppender) request.getLogger().getAppender("web");
	
		return new ResponseEntity<List<String>>(appender.getLines(5), HttpStatus.OK);
	}
}
