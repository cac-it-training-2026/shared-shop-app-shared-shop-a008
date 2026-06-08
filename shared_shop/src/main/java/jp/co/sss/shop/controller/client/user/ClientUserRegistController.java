package jp.co.sss.shop.controller.client.user;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.validation.Valid;
import jp.co.sss.shop.entity.User;
import jp.co.sss.shop.form.UserForm;
import jp.co.sss.shop.repository.UserRepository;

@Controller
public class ClientUserRegistController {
	@Autowired
	UserRepository userrepository;

	@RequestMapping(path = "/client/user/regist/input", method = RequestMethod.GET)
	public String userRegistInput(Model model, @Valid @ModelAttribute UserForm userForm, BindingResult result) {
		if (result.hasErrors()) {
			return "redirect:/regist/input";
		}
		User user = new User();
		BeanUtils.copyProperties(userForm, user, "id");
		user = userrepository.save(user);
		model.addAttribute("NewUser", user);

		return "/resist_input";
	}

	@RequestMapping(path = "/client/user/regist/input", method = RequestMethod.POST)
	public String userRegistInputGET() {
		return "redirect:/regist/input";
	}

}