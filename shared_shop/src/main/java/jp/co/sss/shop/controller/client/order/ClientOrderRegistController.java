package jp.co.sss.shop.controller.client.order;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jp.co.sss.shop.bean.BasketBean;
import jp.co.sss.shop.bean.OrderItemBean;
import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.entity.Item;
import jp.co.sss.shop.entity.User;
import jp.co.sss.shop.form.OrderForm;
import jp.co.sss.shop.repository.ItemRepository;
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

	/** Itemリポジトリのオブジェクトを生成 */
	@Autowired
	ItemRepository itemRepository;

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

		// 届け先入力画面表示
		return "/client/order/address_input";
	}

	/**
	 * @param orderForm
	 * @param result
	 * @param session
	 * @redirect "client/order/payment/input" 支払方法選択画面
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
	 * @return "client/order/payment_input.html" 支払方法選択画面
	 */
	@RequestMapping("/client/order/payment/input")
	public String paymentInputGet(HttpSession session, Model model) {
		// セッションスコープから注文入力フォーム情報を取得
		OrderForm orderForm = (OrderForm) session.getAttribute("orderForm");
		// 注文フォーム情報をリクエストスコープに設定
		model.addAttribute("payMethod", orderForm.getPayMethod());
		// 支払方法選択画面表示
		return "/client/order/payment_input";
	}

	/**
	 * 戻るボタンを押下された時に、支払方法選択画面を表示するメソッド
	 * 
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
	 * @redirect "client/order/check" 注文確認画面
	 */
	@RequestMapping(path = "/client/order/check", method = RequestMethod.POST)
	public String orderCheckPost(HttpSession session, Model model, Integer payMethod) {
		// セッションスコープから注文入力フォーム情報を取得
		OrderForm orderForm = (OrderForm) session.getAttribute("orderForm");
		// 画面から入力された支払方法を取得した注文入力フォーム情報に設定
		orderForm.setPayMethod(payMethod);
		// 注文入力フォーム情報をセッションスコープに保存
		session.setAttribute("orderForm", orderForm);
		// 注文確認画面表示処理へリダイレクト
		return "redirect:/client/order/check";
	}

	/**
	 * @param session
	 * @param model
	 * @return "client/order/check.html" 注文確認画面
	 */
	@RequestMapping("/client/order/check")
	public String orderCheckGet(HttpSession session, Model model) {
		// セッションスコープから注文情報を取得
		OrderForm orderForm = (OrderForm) session.getAttribute("orderForm");

		// セッションスコープから買い物かご情報を取得
		List<BasketBean> basket = (List<BasketBean>) session.getAttribute("basketBeans");
		// 注文商品情報リストを生成
		List<OrderItemBean> orderItemList = new ArrayList<>();
		// 在庫不足の場合のリストを生成
		List<String> itemNameListThan = new ArrayList<>();
		// 在庫が無い場合のリストを生成
		List<String> itemNameListZero = new ArrayList<>();
		// 商品削除用のリストを生成
		List<BasketBean> removeList = new ArrayList<>();

		int zeroCount = 0;

		// 注文商品の最新情報をDBから取得し、商品の在庫チェックを行う
		for (BasketBean basketBean : basket) {
			// 該当商品のエンティティオブジェクトを生成
			Item item = itemRepository.getReferenceById(basketBean.getId());
			// 在庫が無い場合
			if (item.getStock() == 0) {
				zeroCount++; // カウントを増やす
				itemNameListZero.add(basketBean.getName()); // 在庫なしリストに追加
				removeList.add(basketBean); // 削除用リストに追加
			} else if (item.getStock() < basketBean.getOrderNum()) { // 在庫数<注文数の場合
				basketBean.setOrderNum(item.getStock()); // 注文数を現在の在庫数に変更
				itemNameListThan.add(basketBean.getName()); // 在庫不足のリストに追加
			}
		}

		// 全部在庫が無い場合
		if (zeroCount == basket.size()) {
			// セッションから買い物かごを削除
			session.removeAttribute("basketBeans");
		} else if (zeroCount > 0) {
			// 在庫切れの商品をかごから削除
			basket.removeAll(removeList);
			// 削除後の買い物かごをセッションに保存
			session.setAttribute("basketBeans", basket);
		}

		// 買い物かご情報から、商品ごとの金額小計を算出し、注文商品情報リストに保存
		int totalPrice = 0;
		int subTotal = 0;
		for (BasketBean basketBean : basket) {
			Item item = itemRepository.getReferenceById(basketBean.getId());

			subTotal = item.getPrice() * basketBean.getOrderNum();

			OrderItemBean orderItemBean = new OrderItemBean();
			orderItemBean.setName(item.getName());
			orderItemBean.setPrice(item.getPrice());
			orderItemBean.setOrderNum(basketBean.getOrderNum());

		}
		//		・注文商品情報リストから合計金額を算出する
		//		・合計金額をリクエストスコープに設定
		//		・注文商品情報リストをリクエストスコープに設定
		//		・注文入力フォーム情報をリクエストスコープに設定

		// 注文確認画面表示
		return "/client/order/check";
	}

	/**
	 * 
	 * 
	 * @param session
	 * @redirect "client/order/complete" 注文完了画面
	 */
	@RequestMapping(path = "/client/order/complete", method = RequestMethod.POST)
	public String orderCompletePost(HttpSession session) {
		// セッションスコープから注文情報を取得
		OrderItemBean orderItemBean = (OrderItemBean) session.getAttribute("orderItemBeans");
		// セッションスコープから買い物かご情報を取得
		List<BasketBean> basket = (List<BasketBean>) session.getAttribute("basketBeans");
		//		・注文商品の在庫チェックをする
		//		・在庫切れまたは在庫不足の商品がある場合
		//		-注文確認画面表示処理へリダイレクト
		//		リダイレクト:” /client/order/check” 
		//		・注文情報情報を元にDB登録用エンティティオブジェクトを生成
		//		・注文テーブル(Order)および注文商品テーブル(OrderItem)のDB登録実施
		//		・セッションスコープの注文入力フォーム情報と買い物かご情報を削除

		// 注文完了画面表示処理にリダイレクト
		return "redirect:/client/order/complete";
	}

	/**
	 * 注文完了画面を表示するメソッド
	 * 
	 * @param model
	 * @return "client/order/complete.html" 注文完了画面
	 */
	@RequestMapping("/client/order/complete")
	public String orderCompleteGet(Model model) {
		// 注文完了画面表示
		return "/client/order/complete";
	}
}
