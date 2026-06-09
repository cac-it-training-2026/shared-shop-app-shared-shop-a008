package jp.co.sss.shop.controller.client.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jp.co.sss.shop.form.UserForm;

/**
 * 会員管理 登録機能(一般会員)のコントローラクラス
 *
 * @author 難波皐太
 * 
 * TIPS: 一般会員向けの会員登録機能処理です。
 */
@Controller
public class ClientUserRegistController {

	@Autowired
	HttpSession session;

	/**
	 * 新規登録リンククリック時処理
	 * 
	 * @return "redirect:/client/user/regist/input" 
	 */
	@RequestMapping(path = "/client/user/regist/input/init", method = RequestMethod.GET)
	public String showResistInput(Model model) {
		//入力フォーム情報をセッションスコープに保存
		UserForm userForm = (UserForm) session.getAttribute("userForm");

		// 入力フォーム情報が存在しない場合
		if (userForm == null) {

			// 新しい入力フォーム情報を生成
			userForm = new UserForm();
			userForm.setAuthority(2);

			// 入力フォーム情報をセッションスコープに保存
			session.setAttribute("userForm", userForm);
		}

		return "redirect:/client/user/regist/input";

	}

	/**
	 * 新規登録ボタン 押下時処理、確認画面-戻るボタン 押下時処理
	 * 
	 * @return "redirect:/client/user/regist/input" 入力画面　表示処理
	 */
	@RequestMapping(path = "/client/user/regist/input", method = RequestMethod.POST)
	public String userRegistInputPOST() {

		//セッションスコープより入力情報を取り出す
		UserForm userForm = (UserForm) session.getAttribute("userForm");

		if (userForm == null) {

			//入力フォーム情報を新規生成
			userForm = new UserForm();

		}

		//入力フォーム情報をセッションスコープに保存
		session.setAttribute("userForm", userForm);

		return "redirect:/client/user/regist/input";
	}

	/**
	 * 登録画面表示処理
	 * 
	 * @param model Viewとの値受渡し
	 * @return "admin/user/regist_input" 入力画面　表示
	 */
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

	/**
	 * 登録入力確認　処理
	 *
	 * @param form 入力フォーム
	 * @param result 入力値チェックの結果
	 * @return 
	 * 	入力値エラーあり："redirect:/admin/user/regist/input" 入力録画面　表示処理
	 * 	入力値エラーなし："redirect:/admin/user/regist/check" 登録確認画面　表示処理
	 */
	@RequestMapping(path = "/client/user/regist/check", method = RequestMethod.POST)
	public String userRegistCheck(@Valid @ModelAttribute UserForm form, BindingResult result) {
		//セッションスコープから入力フォーム情報を取得
		UserForm SessionForm = (UserForm) session.getAttribute("userForm");
		session.setAttribute("userForm", form);

		if (SessionForm == null) {
			// 入力フォーム情報に不足がある場合、セッションスコープから取得した値をセット
			session.setAttribute("SessionForm", SessionForm);

		}

		//入力エラー情報がある場合
		if (result.hasErrors()) {
			//入力エラー情報と入力フォーム情報を設定
			session.setAttribute("result", result);
			session.setAttribute("userForm", form);

			return "redirect:/client/user/regist/input";
		}

		return "redirect:/client/user/regist/check";
	}

	/**
	 * 登録確認画面　表示処理
	 *
	 * @param model Viewとの値受渡し
	 * @return "admin/user/regist_check" 確認画面　表示
	 */
	@RequestMapping(path = "/client/user/regist/check", method = RequestMethod.GET)
	public String userRegistCheckBack(Model model) {

		//セッションから入力フォーム情報取得
		UserForm userForm = (UserForm) session.getAttribute("userForm");

		//入力フォーム情報をスコープへ設定
		model.addAttribute("userForm", userForm);

		return "/client/user/regist_check";
	}

}