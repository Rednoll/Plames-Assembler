package enterprises.inwaiders.plames.assembler.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AddPartPageController {

	@GetMapping("/addpart")
	public String mainPage(Model model) {
		
		return "add_part";
	}
}
