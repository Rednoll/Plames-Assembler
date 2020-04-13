package enterprises.inwaiders.plames.assembler.spring;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;

@Service
public class ObjectMapperConfigureService {

	@Autowired
	private ObjectMapper mapper;
	
	@PostConstruct
	private void init() {
		
		PolymorphicTypeValidator validator = BasicPolymorphicTypeValidator.builder().build();
	
		mapper.activateDefaultTyping(validator, ObjectMapper.DefaultTyping.NON_FINAL);
	}
}
