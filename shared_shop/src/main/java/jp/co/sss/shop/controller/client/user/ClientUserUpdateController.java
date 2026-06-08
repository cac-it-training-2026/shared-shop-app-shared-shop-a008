package jp.co.sss.shop.controller.client.user;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.form.UserForm;
import jp.co.sss.shop.repository.UserRepository;

/**
 * 会員管理 変更機能(一般会員)のコントローラクラス
 *
 * @author 三浦雅冶
 * 
 * TIPS: 一般会員向けの会員変更機能処理です。
 */
@Controller
public class ClientUserUpdateController {

	/**
	 * 会員情報　リポジトリ
	 */
	@Autowired
	UserRepository userRepository;

	/**
	 * セッション
	 */
	@Autowired
	HttpSession session;

	/**
	 * 入力画面　初期表示処理(POST)
	 * 
	 * @return "redirect:/client/user/update/input" 入力録画面　表示処理
	 */
	@RequestMapping(path = "/client/user/update/input", method = RequestMethod.POST)
	public String updateInputInit() {
		//セッションスコープより入力情報を取り出す
		UserForm userForm = (UserForm) session.getAttribute("userForm");
		if (userForm == null) {
			// 変更対象の情報取得
			UserBean user = (UserBean) session.getAttribute("user");
			if (user == null) {
				// 対象が無い場合、エラー
				return "redirect:/syserror";
			}

			// 初期表示用フォーム情報の生成
			userForm = new UserForm();
			//変更対象の情報をuserFormにコピー
			BeanUtils.copyProperties(user, userForm);

			//変更入力フォームをセッションに保持
			session.setAttribute("userForm", userForm);

		}
		return "redirect:/client/user/update/input";
	}

	/**
	 * 入力画面　表示処理
	 *
	 * @param model Viewとの値受渡し
	 * @return "admin/user/update_input" 変更入力画面 表示
	 */
	@RequestMapping(path = "/client/user/update/input", method = RequestMethod.GET)
	public String updateInput(Model model) {

		//セッションから入力フォーム取得
		UserForm userForm = (UserForm) session.getAttribute("userForm");
		if (userForm == null) {
			// セッション情報がない場合、エラー
			return "redirect:/syserror";
		}
		// 入力フォーム情報を画面表示設定
		model.addAttribute("userForm", userForm);

		BindingResult result = (BindingResult) session.getAttribute("result");
		if (result != null) {
			//セッションにエラー情報がある場合、エラー情報を画面表示設定
			model.addAttribute("org.springframework.validation.BindingResult.userForm", result);
			session.removeAttribute("result");
		}
		return "client/user/update_input";
	}

	/**
	 * 変更確認処理
	 *
	 * @param form 入力フォーム
	 * @param result 入力チェック結果
	 * @return 
	 *   入力値エラーあり："redirect:/client/user/update/input" 変更入力画面へ 
	 *   入力値エラーなし："redirect:/client/user/update/check" 変更確認画面へ
	 */
	@RequestMapping(path = "/client/user/update/check", method = RequestMethod.POST)
	public String updateInputCheck(@Valid @ModelAttribute UserForm form, BindingResult result) {

		//直前のセッション情報を取得
		UserForm lastUserForm = (UserForm) session.getAttribute("userForm");
		if (lastUserForm == null) {
			// セッション情報が無い場合、エラー
			return "redirect:/syserror";
		}
		if (form.getAuthority() == null) {
			//権限情報がない場合、セッション情報から値をセット
			form.setAuthority(lastUserForm.getAuthority());
		}

		// 入力フォーム情報をセッションに保持
		session.setAttribute("userForm", form);

		// 入力値にエラーがあった場合、入力画面に戻る
		if (result.hasErrors()) {

			session.setAttribute("result", result);

			//変更入力画面　表示処理
			return "redirect:/client/user/update/input";

		}
		return "redirect:/client/user/update/check";
	}

	/**
	 * 確認画面　表示処理
	 *
	 * @param model Viewとの値受渡し
	 * @return "client/user/update_check" 確認画面表示
	 */
	@RequestMapping(path = "/client/user/update/check", method = RequestMethod.GET)
	public String updateCheck(Model model) {
		//セッションから入力フォーム情報取得
		UserForm userForm = (UserForm) session.getAttribute("userForm");
		if (userForm == null) {
			// セッション情報がない場合、エラー
			return "redirect:/syserror";
		}
		//入力フォーム情報をスコープへ設定
		model.addAttribute("userForm", userForm);

		// 変更確認画面　表示
		return "client/user/update_check";
	}

	/**
	 * 変更登録、完了画面表示処理
	 *
	 * @return "redirect:/client/user/update/complete" 変更完了画面　表示へ
	 */
	@RequestMapping(path = "/client/user/update/complete", method = RequestMethod.POST)
	public String updateComplete() {
		return "redirect:/client/user/update/complete";
	}

	/**
	 * 変更完了画面　表示
	 * 
	 * @return "client/user/update_complete"
	 */
	@RequestMapping(path = "/client/user/update/complete", method = RequestMethod.GET)
	public String updateCompleteFinish() {
		return "client/user/update_complete";
	}

}
