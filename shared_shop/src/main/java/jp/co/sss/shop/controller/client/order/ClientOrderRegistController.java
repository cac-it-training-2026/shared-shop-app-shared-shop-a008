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
		orderForm.setId(user.getId());
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
	 * @return "client/order/address_input.html" 届け先入力画面
	 */
	@RequestMapping("/client/order/address/input")
	public String addressInputGet(HttpSession session, Model model) {
		// セッションスコープから注文入力フォーム情報を取得
		OrderForm orderForm = (OrderForm) session.getAttribute("orderForm");
		// 注文入力フォーム情報をリクエストスコープに設定
		model.addAttribute("orderForm", orderForm);

		// エラー情報をセッションから取得
		BindingResult result = (BindingResult) session.getAttribute("errors");

		// セッションスコープに入力エラー情報がある場合
		if (result != null) {
			// 取得したエラー情報をリクエストスコープに設定
			model.addAttribute("org.springframework.validation.BindingResult.orderForm", result);
			// セッションスコープから、エラー情報を削除
			session.removeAttribute("errors");
		}

		// 登録画面表示
		return "/client/order/address_input";
	}

	/**
	 * @param orderForm
	 * @param result
	 * @param session
	 * @redirect "client/order/payment/input"
	 */
	@RequestMapping(path = "/client/order/payment/input", method = RequestMethod.POST)
	public String paymentInputPost(@Valid @ModelAttribute OrderForm orderForm, BindingResult result,
			HttpSession session) {

		// セッションスコープから注文入力フォーム情報を取得
		orderForm = (OrderForm) session.getAttribute("orderForm");
		// BindingResultオブジェクトに入力エラー情報がある場合
		if (result.hasErrors()) {
			// 入力エラー情報をセッションスコープに設定
			session.setAttribute("errors", result);
			// 届け先入力画面表示処理にリダイレクト
			return "redirect:/client/order/address/input";
		}

		// 支払方法選択画面表示処理にリダイレクト
		return "redirect:/client/order/payment/input";
	}

	/**
	 * @param session
	 * @param model
	 * @return
	 */
	@RequestMapping("/client/order/payment/input")
	public String paymentInputGet(HttpSession session, Model model) {
		//		・セッションスコープから注文入力フォーム情報を取得
		OrderForm orderForm = (OrderForm) session.getAttribute("orderForm");
		//		・注文フォーム情報をリクエストスコープに設定
		model.addAttribute("payMethod", orderForm.getPayMethod());
		//		・支払方法選択画面表示
		return "/client/order/payment_input";
	}

	/**
	 * @redirect "client/order/address/input" 支払方法選択画面
	 */
	@RequestMapping(path = "/client/order/payment/back", method = RequestMethod.POST)
	public String paymentBack() {
		// 支払い方法選択画面 表示処理へリダイレクト
		return "redirect:/client/order/address/input";
	}

	/**
	 * @param session
	 * @param model
	 * @redirect
	 */
	@RequestMapping(path = "/client/order/check", method = RequestMethod.POST)
	public String orderCheckPost(HttpSession session, Model model) {
		//		・セッションスコープから注文入力フォーム情報を取得
		//		・画面から入力された支払方法を取得した注文入力フォーム情報に設定
		//		・注文入力フォーム情報をセッションスコープに保存
		//		・注文確認画面表示処理へリダイレクト
		return "redirect:/client/order/check";
	}

	/**
	 * @param session
	 * @param model
	 * @return
	 */
	@RequestMapping("/client/order/check")
	public String orderCheckGet(HttpSession session, Model model) {
		//		・セッションスコープから注文情報を取得
		//		・セッションスコープから買い物かご情報を取得
		//		・注文商品の最新情報をDBから取得し、商品の在庫チェックを行う
		//		・在庫不足、在庫切れ商品がある場合
		//		- 注文警告メッセージをリクエストスコープに保存
		//		- 在庫数にあわせて、買い物かご情報を更新（注文数、在庫数）
		//		- 在庫切れの商品は、買い物かごから削除
		//		・在庫状況を反映した買い物かご情報をセッションに保存
		//		・買い物かご情報から、商品ごとの金額小計を算出し、注文商品情報リストに保存
		//		・注文商品情報リストから合計金額を算出する
		//		・合計金額をリクエストスコープに設定
		//		・注文商品情報リストをリクエストスコープに設定
		//		・注文入力フォーム情報をリクエストスコープに設定
		//		・注文確認画面表示
		return "/client/order/check";
	}

	/**
	 * @param session
	 * @redirect
	 */
	@RequestMapping(path = "/client/order/complete", method = RequestMethod.POST)
	public String orderCompletePost(HttpSession session) {
		//		・セッションスコープから注文情報を取得
		//		・セッションスコープから買い物かご情報を取得
		//		・注文商品の在庫チェックをする
		//		・在庫切れまたは在庫不足の商品がある場合
		//		-注文確認画面表示処理へリダイレクト
		//		リダイレクト:” /client/order/check” 
		//		・注文情報情報を元にDB登録用エンティティオブジェクトを生成
		//		・注文テーブルおよび注文商品テーブルのDB登録実施
		//		・セッションスコープの注文入力フォーム情報と買い物かご情報を削除
		//		・注文完了画面表示処理にリダイレクト

		return "redirect:/client/order/complete";
	}

	/**
	 * @param model
	 * @return
	 */
	@RequestMapping("/client/order/complete")
	public String orderCompleteGet(Model model) {
		//		・注文完了画面表示
		return "/client/order/complete";
	}
}
