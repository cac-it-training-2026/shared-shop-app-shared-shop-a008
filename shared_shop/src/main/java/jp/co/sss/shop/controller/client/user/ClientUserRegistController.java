package jp.co.sss.shop.controller.client.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpSession;
import jp.co.sss.shop.form.UserForm;

@Controller
public class ClientUserRegistController {

	@Autowired
	HttpSession session;

	//新規登録リンククリック時処理
	@RequestMapping(path = "/client/user/regist/input/init", method = RequestMethod.GET)
	public String showResistInput(Model model) {
		UserForm userForm = (UserForm) session.getAttribute("userForm");

		if (userForm == null) {
			userForm = new UserForm();
			userForm.setAuthority(2);
			session.setAttribute("userForm", userForm);
		}

		return "redirect:/client/user/regist/input";

	}

	//新規登録ボタン 押下時処理、確認画面-戻るボタン 押下時処理
	@RequestMapping(path = "/client/user/regist/input", method = RequestMethod.POST)
	public String userRegistInputPOST() {

		//セッションスコープより入力情報を取り出す
		UserForm userForm = (UserForm) session.getAttribute("userForm");

		if (userForm == null) {

			//入力フォーム情報を新規生成
			userForm = new UserForm();

		}

		//必要な情報を入力フォーム情報にセット
		userForm.setEmail(userForm.getEmail());
		userForm.setName(userForm.getName());
		userForm.setPassword(userForm.getPassword());
		userForm.setPostalCode(userForm.getPostalCode());
		userForm.setAddress(userForm.getAddress());
		userForm.setPhoneNumber(userForm.getPhoneNumber());

		//入力フォーム情報をセッションスコープに保存
		session.setAttribute("userForm", userForm);

		return "redirect:/client/user/regist/input";
	}

	//登録画面表示処理
	@RequestMapping(path = "/client/user/regist/input", method = RequestMethod.GET)
	public String userRegistInputGET(Model model) {

		//セッションスコープから入力フォーム情報を取得
		UserForm userForm = (UserForm) session.getAttribute("userForm");

		//入力エラー確認
		BindingResult result = (BindingResult) session.getAttribute("result");

		if (result != null) {

			//セッションにエラー情報がある場合、エラー情報をリクエストスコープに設定
			model.addAttribute("org.springframework.validation.BindingResult.userForm", result);

			//セッションからエラー情報を削除
			session.removeAttribute("result");
		}

		// 入力フォーム情報をリクエストスコープに設定
		model.addAttribute("userForm", userForm);

		return "/client/user/regist_input";
	}

	//確認ボタン 押下時処理
	@RequestMapping(path = "/client/user/regist/check", method = RequestMethod.POST)
	public String userRegistCheck() {
		return "/regist_check";
	}

}