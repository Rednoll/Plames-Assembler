package com.inwaiders.plames.assembler.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PlamesAssemblerController {

	@GetMapping("/")
	public String mainPage(Model model) {
		
		return "wizard";
	}
}
