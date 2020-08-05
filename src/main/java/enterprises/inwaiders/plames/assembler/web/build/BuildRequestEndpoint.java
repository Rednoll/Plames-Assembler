package enterprises.inwaiders.plames.assembler.web.build;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.async.DeferredResult;

import enterprises.inwaiders.plames.assembler.domain.build.BuildRequest;
import enterprises.inwaiders.plames.assembler.domain.build.BuildRequest.Status;
import enterprises.inwaiders.plames.assembler.domain.build.BuildRequestProcessor;
import enterprises.inwaiders.plames.assembler.domain.build.BuildRequestWaitData;
import enterprises.inwaiders.plames.assembler.domain.build.BuildStep;
import enterprises.inwaiders.plames.assembler.dto.compile.CompileRequestDto;
import enterprises.inwaiders.plames.assembler.web.WebCompileLogAppender;
import enterprises.inwaiders.plames.eco.domain.user.User;

@Controller
@ResponseBody
@RequestMapping("/ajax/request")
public class BuildRequestEndpoint {
	
	private static Map<String, BuildRequest> requests = new HashMap<>();
	
	@PostMapping("/create")
	public ResponseEntity<Boolean> create(@RequestBody CompileRequestDto requestDto, Principal principal) {
		
		String userName = principal.getName();
		User user = User.findByCredentialsMainLogin(userName);
		
		if(requests.get(userName) != null) {
			
			BuildRequest oldReqeust = requests.get(userName);
			
			if(oldReqeust.isBuilded()) {
				
				requests.remove(userName);
			}
			else {
				
				return ResponseEntity.status(HttpStatus.CONFLICT).body(false);
			}
		}
		
		BuildRequest request = BuildRequestProcessor.createRequest(user);
		
		request.loadFromDto(requestDto);
		
		requests.put(userName, request);
		
		return ResponseEntity.ok().body(true);
	}
	
	@GetMapping("/build")
	public ResponseEntity<Integer> build(Principal principal) {
		
		String userName = principal.getName();
		
		BuildRequest request = requests.get(userName);
		
		if(request == null) return ResponseEntity.notFound().build();
		
		int placeInQueue = BuildRequestProcessor.build(request);
	
		return new ResponseEntity<Integer>(placeInQueue, HttpStatus.OK);
	}
	
	@GetMapping("/steps")
	public ResponseEntity<List<BuildStep.BaseDto>> steps(Principal principal) {
		
		String userName = principal.getName();
		
		BuildRequest request = requests.get(userName);
		
		if(request == null) return ResponseEntity.notFound().build();
		
		List<BuildStep.BaseDto> dtos = request.getSteps().stream().map(BuildStep::toDto).collect(Collectors.toList());
		
		return new ResponseEntity<List<BuildStep.BaseDto>>(dtos, HttpStatus.OK);
	}
	
	@GetMapping("/wait_step_change")
	public DeferredResult<ResponseEntity<BuildStep.BaseDto>> waitStep(String name, Principal principal) {
	
		String userName = principal.getName();
		
		BuildRequest request = requests.get(userName);
		
		DeferredResult<ResponseEntity<BuildStep.BaseDto>> result = new DeferredResult<ResponseEntity<BuildStep.BaseDto>>(3600000L);
		
		if(request == null) {
			
			result.setResult(ResponseEntity.notFound().build());
			return result;
		}
		
		request.addCurrentStepChangeListener(step -> result.setResult(ResponseEntity.ok().body(step.toDto())));

		return result;
	}
	
	@GetMapping("/wait_build_end")
	public DeferredResult<ResponseEntity<Boolean>> waitComplete(Principal principal) {
	
		String userName = principal.getName();
		
		BuildRequest request = requests.get(userName);
		
		DeferredResult<ResponseEntity<Boolean>> result = new DeferredResult<ResponseEntity<Boolean>>(3600000L);
		
		if(request == null) {
			
			result.setResult(ResponseEntity.notFound().build());
			return result;
		}
		
		request.addCompleteListener(req -> result.setResult(ResponseEntity.ok().body(req.getStatus() == Status.SUCCESS)));
	
		return result;
	}
	
	@GetMapping("/place_in_queue")
	public ResponseEntity<BuildRequestWaitData> getPlaceInQueue(Principal principal) {
		
		String userName = principal.getName();
		
		BuildRequest request = requests.get(userName);
		
		if(request == null) return ResponseEntity.notFound().build();
		
		return ResponseEntity.ok().body(BuildRequestProcessor.getRequestWaitData(request));
	}
	
	@GetMapping("/status")
	public ResponseEntity<BuildRequest.Status> status(Principal principal) {
		
		String userName = principal.getName();
		
		BuildRequest request = requests.get(userName);
		
		if(request == null) return ResponseEntity.notFound().build();
		
		return new ResponseEntity<BuildRequest.Status>(request.getStatus(), HttpStatus.OK);
	}
	
	@GetMapping("/build_log_news")
	public ResponseEntity<List<String>> compileLog(Principal principal, @RequestParam int linesCount) {
		
		String userName = principal.getName();
		
		BuildRequest request = requests.get(userName);

		if(request == null) return ResponseEntity.notFound().build();
		
		WebCompileLogAppender appender = (WebCompileLogAppender) request.getLogger().getAppender("web");
	
		return new ResponseEntity<List<String>>(appender.getLines(linesCount), HttpStatus.OK);
	}
	
	@GetMapping(value = "/download", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public ResponseEntity<FileSystemResource> compileLog(Principal principal) {
		
		String userName = principal.getName();
		
		BuildRequest request = requests.get(userName);

		if(request == null) return ResponseEntity.notFound().build();
	
		return new ResponseEntity<FileSystemResource>(new FileSystemResource(request.getBundle()), HttpStatus.OK);
	}
}
