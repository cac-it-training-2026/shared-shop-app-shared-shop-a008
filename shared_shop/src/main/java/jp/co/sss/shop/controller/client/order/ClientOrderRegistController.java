package jp.co.sss.shop.controller.client.order;

import java.util.ArrayList;
import java.util.List;

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
import jp.co.sss.shop.bean.BasketBean;
import jp.co.sss.shop.bean.OrderItemBean;
import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.entity.Item;
import jp.co.sss.shop.entity.Order;
import jp.co.sss.shop.entity.OrderItem;
import jp.co.sss.shop.entity.User;
import jp.co.sss.shop.form.OrderForm;
import jp.co.sss.shop.repository.ItemRepository;
import jp.co.sss.shop.repository.OrderItemRepository;
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

	/** OrderItemリポジトリのオブジェクトを生成 */
	@Autowired
	OrderItemRepository orderItemRepository;

	/**
	 * 
	 * 
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
		List<String> itemNameListLessThan = new ArrayList<>();
		// 在庫が無い場合のリストを生成
		List<String> itemNameListZero = new ArrayList<>();
		// 商品削除用のリストを生成
		List<BasketBean> removeList = new ArrayList<>();

		// 在庫が0の商品をカウントする変数
		int zeroCount = 0;

		// 注文商品の最新情報をDBから取得し、商品の在庫チェックを行う
		// 拡張for文で買い物かごリストの中身をチェック
		for (BasketBean basketBean : basket) {
			// 該当商品のエンティティオブジェクトを生成
			Item item = itemRepository.getReferenceById(basketBean.getId());
			// 在庫が無い場合
			if (item.getStock() == 0) {
				zeroCount++; // カウントを増やす
				// 在庫なしリストに追加
				itemNameListZero.add(basketBean.getName());
				// 削除用リストに追加
				removeList.add(basketBean);
			} else if (item.getStock() < basketBean.getOrderNum()) { // 在庫数<注文数の場合
				// 注文数を現在の在庫数に変更
				basketBean.setOrderNum(item.getStock());
				// 在庫不足のリストに追加
				itemNameListLessThan.add(basketBean.getName());
				// 在庫不足リストをリクエストスコープに保存
				model.addAttribute("itemNameListLessThan", itemNameListLessThan);
			}
		}

		// 全部在庫が無い場合
		if (zeroCount == basket.size()) {
			// セッションから買い物かごを削除
			session.removeAttribute("basketBeans");
			// 在庫なしリストに追加
			model.addAttribute("itemNameListZero", itemNameListZero);
		} else if (zeroCount > 0) {
			// 在庫切れの商品をかごから削除
			basket.removeAll(removeList);
			// 削除後の買い物かごをセッションに保存
			session.setAttribute("basketBeans", basket);
			// 在庫なしリストをリクエストスコープに保存
			model.addAttribute("itemNameListZero", itemNameListZero);
		}

		// 合計金額を代入する変数
		int totalPrice = 0;
		// 小計を代入する変数
		int subTotal = 0;

		// 買い物かご情報から、商品ごとの金額小計を算出し、注文商品情報リストに保存
		// 拡張for文で買い物かごリストの中身をチェック
		for (BasketBean basketBean : basket) {
			// 該当商品のエンティティオブジェクトを生成
			Item item = itemRepository.getReferenceById(basketBean.getId());

			// 商品単価x注文数 で 小計を計算
			subTotal = item.getPrice() * basketBean.getOrderNum();

			// 注文商品のBeanオブジェクトを生成
			OrderItemBean orderItemBean = new OrderItemBean();

			// Beanに商品の情報をコピー
			orderItemBean.setName(item.getName());
			orderItemBean.setPrice(item.getPrice());
			orderItemBean.setImage(item.getImage());
			orderItemBean.setOrderNum(basketBean.getOrderNum());
			orderItemBean.setSubtotal(subTotal);

			// 注文商品情報リストに追加
			orderItemList.add(orderItemBean);

			// 合計金額に追加
			totalPrice += subTotal;
		}

		// 合計金額をリクエストスコープに設定
		model.addAttribute("total", totalPrice);

		// 注文商品情報リストをスコープに設定
		if (zeroCount != basket.size()) {
			model.addAttribute("orderItemBeans", orderItemList);
			session.setAttribute("orderItemBeans", orderItemList);
		}
		// 注文入力フォーム情報をリクエストスコープに設定
		model.addAttribute("orderForm", orderForm);
		// 注文確認画面表示
		return "/client/order/check";
	}

	/**
	 * 注文情報を登録するメソッド
	 * 
	 * @param session
	 * @redirect "client/order/complete" 注文完了画面
	 */
	@RequestMapping(path = "/client/order/complete", method = RequestMethod.POST)
	public String orderCompletePost(HttpSession session, Model model) {
		// セッションスコープから買い物かご情報を取得
		List<BasketBean> basket = (List<BasketBean>) session.getAttribute("basketBeans");

		// セッションスコープから注文入力情報を取得
		OrderForm orderForm = (OrderForm) session.getAttribute("orderForm");
		List<OrderItemBean> orderItemList = (List<OrderItemBean>) session.getAttribute("orderItemBeans");
		List<OrderItem> orderItems = new ArrayList();
		BeanUtils.copyProperties(orderItemList, orderItems);

		// セッションスコープからログイン会員情報を取得
		UserBean loginUser = (UserBean) session.getAttribute("user");
		// // 取得したログイン会員情報のユーザIDを条件にDBからユーザ情報を取得
		User user = userRepository.getReferenceById(loginUser.getId());

		// 在庫不足の場合のリストを生成
		List<String> itemNameListLessThan = new ArrayList<>();
		// 在庫が無い場合のリストを生成
		List<String> itemNameListZero = new ArrayList<>();
		// 商品削除用のリストを生成
		List<BasketBean> removeList = new ArrayList<>();

		// 在庫が0の商品をカウントする変数
		int zeroCount = 0;

		// 注文商品の在庫チェックをする
		// 拡張for文で買い物かごリストの中身をチェック
		for (BasketBean basketBean : basket) {
			// 該当商品のエンティティオブジェクトを生成
			Item item = itemRepository.getReferenceById(basketBean.getId());
			// 在庫が無い場合
			if (item.getStock() == 0) {
				// カウントを増やす
				zeroCount++;
				// 在庫なしリストに追加
				itemNameListZero.add(basketBean.getName());
				// 削除用リストに追加
				removeList.add(basketBean);
			} else if (item.getStock() < basketBean.getOrderNum()) { // 在庫数<注文数の場合
				// 注文数を現在の在庫数に変更
				basketBean.setOrderNum(item.getStock());
				// 在庫不足のリストに追加
				itemNameListLessThan.add(basketBean.getName());
				// 在庫不足リストをリクエストスコープに保存
				model.addAttribute("itemNameListLessThan", itemNameListLessThan);
				// 注文確認画面にリダイレクト
				return "redirect:/client/order/check";
			}
		}

		// 全部在庫が無い場合
		if (zeroCount == basket.size()) {
			// セッションから買い物かごを削除
			session.removeAttribute("basketBeans");
			// 在庫なしリストに追加
			model.addAttribute("itemNameListZero", itemNameListZero);
			return "redirect:/client/order/check";
		} else if (zeroCount > 0) {
			// 在庫切れの商品をかごから削除
			basket.removeAll(removeList);
			// 削除後の買い物かごをセッションに保存
			session.setAttribute("basketBeans", basket);
			// 在庫なしリストに追加
			model.addAttribute("itemNameListZero", itemNameListZero);
			// 注文確認画面にリダイレクト
			return "redirect:/client/order/check";
		}

		// 注文テーブル(Order)および注文商品テーブル(OrderItem)のDB登録実施
		// 注文情報を元にDB登録用エンティティオブジェクトを生成
		Order order = new Order();
		BeanUtils.copyProperties(orderForm, order, "id", "insertDate", "user", "orderItemsList");
		order.setInsertDate(new java.sql.Date(new java.util.Date().getTime()));
		order.setUser(user);
		order.setOrderItemsList(orderItems);
		order = orderRepository.save(order);

		for (BasketBean basketBean : basket) {
			Item item = itemRepository.getReferenceById(basketBean.getId());
			OrderItem orderItem = new OrderItem();
			orderItem.setQuantity(basketBean.getOrderNum());
			orderItem.setOrder(order);
			orderItem.setItem(item);
			orderItem.setPrice(item.getPrice());
			orderItem = orderItemRepository.save(orderItem);
		}

		// 買い物かご情報を削除
		session.removeAttribute("basketBeans");
		// 注文入力フォーム情報を削除
		session.removeAttribute("orderForm");

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
