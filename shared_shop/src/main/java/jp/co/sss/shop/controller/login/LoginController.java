package jp.co.sss.shop.controller.login;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.entity.User;
import jp.co.sss.shop.form.LoginForm;
import jp.co.sss.shop.repository.UserRepository;
import jp.co.sss.shop.util.Constant;

/**
 * ログイン機能のコントローラクラス
 *
 * @author SystemShared
 */
@Controller
public class LoginController {

	/**
	 * 乱数生成器
	 */
	private static final Random RANDOM = new Random();

	/**
	 * 会員情報
	 */
	@Autowired
	UserRepository userRepository;

	/**
	 * セッション情報
	 */
	@Autowired
	HttpSession session;

	/**
	 * ログイン処理
	 *
	 * @param form ログインフォーム
	 * @return "login" ログイン画面表示
	 */
	@RequestMapping(path = "/login", method = RequestMethod.GET)
	public String login(@ModelAttribute LoginForm form) {

		// セッション情報を無効にする
		session.invalidate();

		return "login";
	}

	/**
	 * ログイン処理
	 *
	 * @param form ログインフォーム
	 * @param result 入力チェック結果
	 * @return
			一般会員の場合 "redirect:/" トップ画面表示処理
			運用管理者、システム管理者の場合 "redirect:/adminmenu"管理者メニュー表示処理
	 */
	@RequestMapping(path = "/login", method = RequestMethod.POST)
	public String doLogin(@Valid @ModelAttribute LoginForm form, BindingResult result) {

		String returnStr = "login";
		if (result.hasErrors()) {
			// 入力値に誤りがあった場合
			// セッション情報を無効にして、ログイン画面再表示
			session.invalidate();
			returnStr = "login";

		} else {
			// 会員情報を取得
			User user = userRepository.findByEmailAndDeleteFlag(form.getEmail(), Constant.NOT_DELETED);

			// UserBeanに情報をコピー
			UserBean userBean = new UserBean();
			userBean.setId(user.getId());
			userBean.setName(user.getName());
			userBean.setAuthority(user.getAuthority());
			userBean.setPoint(user.getPoint());

			// セッションにログイン情報を登録
			session.setAttribute("user", userBean);

			// 権限を取得
			Integer authority = userBean.getAuthority();

			if (authority.intValue() == Constant.AUTH_CLIENT) {

				// おみくじ処理
				String omikujiResult = "";
				int bonusPoint = 0;

				int randomNum = RANDOM.nextInt(100);
				if (randomNum < 10) { // 10%
					omikujiResult = "大吉";
					bonusPoint = 30;
				} else if (randomNum < 30) { // 20%
					omikujiResult = "中吉";
					bonusPoint = 20;
				} else if (randomNum < 50) { // 20%
					omikujiResult = "小吉";
					bonusPoint = 10;
				} else if (randomNum < 80) { // 30%
					omikujiResult = "吉";
					bonusPoint = 5;
				} else { // 20%
					omikujiResult = "凶";
					bonusPoint = 0;
				}

				// DB更新
				Integer currentPoint = user.getPoint();
				if (currentPoint == null) {
					currentPoint = 0;
				}
				user.setPoint(currentPoint + bonusPoint);
				userRepository.save(user);

				// セッションに保存
				session.setAttribute("omikujiResult", omikujiResult);
				session.setAttribute("bonusPoint", bonusPoint);

				// セッションのUserBeanも更新
				userBean.setPoint(user.getPoint());

				// 一般会員ログインした場合、トップ画面表示処理にリダイレクト
				returnStr = "redirect:/";
			} else {

				// 運用管理者、もしくはシステム管理者としてログインした場合、管理者用メニュー画面表示処理にリダイレクト
				returnStr = "redirect:/admin/menu";
			}
		}
		return returnStr;

	}

	/**
	 * 管理者メニュー表示処理
	 *
	 * @return "admin/menu" 管理者メニュー画面表示
	 */
	@RequestMapping(path = "/admin/menu", method = RequestMethod.GET)
	public String showAdminMenu() {

		// 管理者用メニュー画面表示
		return "admin/admin_menu";
	}

}
