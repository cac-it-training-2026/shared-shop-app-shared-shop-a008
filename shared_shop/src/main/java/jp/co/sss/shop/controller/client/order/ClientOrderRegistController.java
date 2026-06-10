package jp.co.sss.shop.controller.client.order;

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
import jp.co.sss.shop.entity.User;
import jp.co.sss.shop.form.OrderForm;
import jp.co.sss.shop.repository.OrderRepository;
import jp.co.sss.shop.repository.UserRepository;

/**
 * 注文登録の基本クラス
 * 
 * @author 諸星愛実
 */
@Controller
public class ClientOrderRegistController {

	/** Orderリポジトリのオブジェクトを生成 */
	@Autowired
	OrderRepository orderRepository;

	/** Userリポジトリのオブジェクトを生成 */
	@Autowired
	UserRepository userRepository;

	/**
	 * @param session
	 * @redirect "client/order/address/input" 届け先入力画面
	 */
	@RequestMapping(path = "/client/order/address/input", method = RequestMethod.POST)
	public String addressInputPost(HttpSession session) {

		// ログイン会員情報をセッションから取得
		UserBean loginUser = (UserBean) session.getAttribute("user");

		// 取得したログイン会員情報のユーザIDを条件にDBからユーザ情報を取得
		User user = userRepository.getReferenceById(loginUser.getId());

		// 注文入力フォームを生成
		OrderForm orderForm = new OrderForm();

		// 取得ユーザー情報をフォームにコピー
		orderForm.setPostalCode(user.getPostalCode());
		orderForm.setAddress(user.getAddress());
		orderForm.setName(user.getName());
		orderForm.setPhoneNumber(user.getPhoneNumber());

		// 支払方法の初期値としてクレジットカードを設定
		orderForm.setPayMethod(1);

		// セッションに保存
		session.setAttribute("orderForm", orderForm);

		// 届け先入力画面表示処理へリダイレクト
		return "redirect:/client/order/address/input";
	}

	/**
	 * 届け先入力画面を表示するメソッド
	 * 
	 * @param session
	 * @param model
	 * @return "/client/order/address_input.html" 届け先入力画面
	 */
	@RequestMapping("/client/order/address/input")
	public String addressInputGet(HttpSession session, Model model) {
		// セッションスコープから注文入力フォーム情報を取得
		OrderForm orderForm = (OrderForm) session.getAttribute("orderForm");
		// 注文入力フォーム情報をリクエストスコープに設定
		model.addAttribute("orderForm", orderForm);

		// memo
		//		・セッションスコープに入力エラー情報がある場合
		//		　- 取得したエラー情報をリクエストスコープに設定
		//		　- セッションスコープから、エラー情報を削除

		// 登録画面表示
		return "/client/order/address_input";
	}

	/**
	 * @param orderForm
	 * @param result
	 * @param session
	 * @redirect
	 */
	@RequestMapping(path = "/client/order/payment/input", method = RequestMethod.POST)
	public String paymentInputPost(@Valid @ModelAttribute OrderForm orderForm, BindingResult result,
			HttpSession session) {
		return "redirect:/client/order/payment/input";
	}

	/**
	 * @param session
	 * @param model
	 * @return
	 */
	@RequestMapping("/client/order/payment/input")
	public String paymentInputGet(HttpSession session, Model model) {
		return "/client/order/payment_input";
	}

	/**
	 * @redirect
	 */
	@RequestMapping(path = "/client/order/payment/back", method = RequestMethod.POST)
	public String paymentBack() {
		return "redirect:/client/order/address/input";
	}

	/**
	 * @param session
	 * @param model
	 * @redirect
	 */
	@RequestMapping(path = "/client/order/check", method = RequestMethod.POST)
	public String orderCheckPost(HttpSession session, Model model) {
		return "redirect:/client/order/check";
	}

	/**
	 * @param session
	 * @param model
	 * @return
	 */
	@RequestMapping("/client/order/check")
	public String orderCheckGet(HttpSession session, Model model) {
		return "/client/order/check";
	}

	/**
	 * @param session
	 * @redirect
	 */
	@RequestMapping(path = "/client/order/complete", method = RequestMethod.POST)
	public String orderCompletePost(HttpSession session) {
		return "redirect:/client/order/complete";
	}

	/**
	 * @param model
	 * @return
	 */
	@RequestMapping("/client/order/complete")
	public String orderCompleteGet(Model model) {
		return "/client/order/complete";
	}
}
