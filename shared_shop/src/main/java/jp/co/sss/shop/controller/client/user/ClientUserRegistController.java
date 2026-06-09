package jp.co.sss.shop.controller.client.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpSession;
import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.form.UserForm;
import jp.co.sss.shop.repository.UserRepository;

@Controller
public class ClientUserRegistController {
	@Autowired
	UserRepository userrepository;

	@RequestMapping(path = "/regist/input/init", method = RequestMethod.GET)
	public String userRegistInput(HttpSession session) {

		//入力フォーム情報を新規生成
		UserForm userForm = new UserForm();
		//入力フォーム情報をセッションスコープに保存
		session.setAttribute("user", userForm);

		return "redirect://resist/input";
	}

	@RequestMapping(path = "/regist/input", method = RequestMethod.POST)
	public String userRegistInputGET(HttpSession session) {

		//セッションスコープより入力情報を取り出す
		UserForm userForm = (UserForm) session.getAttribute("userForm");
		if (userForm == null) {
			userForm = new UserForm();
			userForm.setAuthority(((UserBean) session.getAttribute("user")).getAuthority());

			//空の入力フォーム情報をセッションに保持 登録ボタンからの遷移
			session.setAttribute("userForm", userForm);
		}
		return "redirect:/regist/input";
	}

}