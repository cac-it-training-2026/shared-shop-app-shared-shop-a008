package jp.co.sss.shop.controller.client.basket;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpSession;
import jp.co.sss.shop.bean.BasketBean;
import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.entity.Item;
import jp.co.sss.shop.repository.ItemRepository;

/**
 * 買い物かごの基本クラス
 * 
 * @author 諸星愛実
 */
@Controller
public class ClientBasketController {

	/** リポジトリのオブジェクトを生成 */
	@Autowired
	ItemRepository itemRepository;

	/** セッションオブジェクト生成 */
	@Autowired
	HttpSession session;

	/**
	 * 買い物かご内の商品一覧を表示するメソッド
	 * 
	 * @param session セッション情報
	 * @return "client/basket/list.html" 買い物かごの内容表示
	 */
	@RequestMapping(path = "/client/basket/list", method = RequestMethod.GET)
	public String basketList(Model model) {

		// ログインユーザーの取得
		UserBean loginUser = (UserBean) session.getAttribute("user");
		// ログインユーザーがnullの場合
		if (loginUser == null) {
			return "redirect:/login"; // ログイン画面にリダイレクト
		}

		// 買い物かごリストを
		List<BasketBean> basket = (List<BasketBean>) session.getAttribute("basketBeans");

		// 在庫不足の場合のリストを生成
		List<String> itemNameListLessThan = new ArrayList<>();
		// 在庫が無い場合のリストを生成
		List<String> itemNameListZero = new ArrayList<>();

		// 在庫切れの際に、買い物かごから削除するためのリストを生成
		List<BasketBean> removeList = new ArrayList<>();

		// 買い物かごリストがある場合
		if (basket != null) {
			// 拡張for文で買い物かごリストの中身をチェック
			for (BasketBean basketBean : basket) {
				// 該当商品のエンティティオブジェクトを生成
				Item item = itemRepository.getReferenceById(basketBean.getId());

				// 在庫が無い場合
				if (item.getStock() == 0) {
					// 在庫なしリストに追加
					itemNameListZero.add(basketBean.getName());
					// 削除用リストに追加
					removeList.add(basketBean);
				} else if (basketBean.getOrderNum() > item.getStock()) { // 買い物かごの数量が在庫数より多い場合
					// 在庫不足リストに追加
					itemNameListLessThan.add(basketBean.getName());
					// 現在の在庫数まで減らす
					basketBean.setOrderNum(item.getStock());
				}

				// 表示用在庫数を最新化
				basketBean.setStock(item.getStock());
			}

			// 在庫切れのものを買い物かごから削除
			basket.removeAll(removeList);
			// セッションに保存
			session.setAttribute("basketBeans", basket);
		}

		// リクエストスコープに保存
		model.addAttribute("itemNameListLessThan", itemNameListLessThan);
		model.addAttribute("itemNameListZero", itemNameListZero);

		// templates/client/basket/list.htmlに遷移
		updateBasketSummary(session);
		return "client/basket/list";

	}

	/**
	 * 戻るボタンを押下された時に、買い物かご画面を表示するメソッド
	 * 
	 * @redirect "client/basket/list" 買い物かご画面
	 */
	@RequestMapping("/client/basket/list")
	public String basketBack() {
		// 買い物かご画面にリダイレクト
		return "redirect:/client/basket/list";
	}

	/**
	 * 買い物かごに商品追加をするメソッド
	 * 
	 * @param session セッション情報
	 * @param id 追加する商品ID
	 * @redirect "client/basket/list" 買い物かご表示にリダイレクト
	 */
	@RequestMapping(path = "/client/basket/add", method = RequestMethod.POST)
	public String basketAdd(Integer id) {
		// TODO: Issue #2 数量指定機能の実装
		// - 引数に注文数(orderNum)を追加
		// - 在庫数チェックのロジックを追加
		// 買い物かごリストを取得
		List<BasketBean> basket = (List<BasketBean>) session.getAttribute("basketBeans");

		// 買い物かごリストが存在しない場合
		if (basket == null) {
			// 空の買い物かごリストを生成
			basket = new ArrayList<BasketBean>();
		}

		// getReferenceById(id)で主キー検索
		Item item = itemRepository.getReferenceById(id);

		// 同一商品が存在するかのフラグ
		boolean exist = false;

		// 拡張for文で買い物かごリストの中身をチェック
		for (BasketBean existBasketBeans : basket) {
			// 既存買い物かごの商品IDと、選択商品IDが同じ場合
			if (existBasketBeans.getId() == item.getId()) {
				existBasketBeans.setOrderNum(existBasketBeans.getOrderNum() + 1);

				// フラグをtrueに設定
				exist = true;
				// ループを抜ける
				break;
			}
		}
		// 買い物かごに同一商品が存在しない場合
		if (!exist) {
			// BasketBeanオブジェクトを生成
			BasketBean basketBean = new BasketBean();
			// 商品ID, 商品名, 在庫数をBeanにコピー
			basketBean.setId(item.getId());
			basketBean.setName(item.getName());
			basketBean.setStock(item.getStock());
			// 買い物かごリストに追加
			basket.add(basketBean);
		}

		// セッションスコープに保存
		session.setAttribute("basketBeans", basket);

		updateBasketSummary(session);

		// 買い物かごリストにリダイレクト
		return "redirect:/client/basket/list";
	}

	/**
	 * 買い物かごの商品を削除するメソッド
	 * 
	 * @param session セッション情報
	 * @param id 削除する商品のID
	 * @redirect "client/basket/list" 買い物かご表示にリダイレクト
	 */
	@RequestMapping(path = "/client/basket/delete", method = RequestMethod.POST)
	public String basketDelete(Integer id) {
		// 買い物かごリストを取得
		List<BasketBean> basket = (List<BasketBean>) session.getAttribute("basketBeans");

		// 拡張for文で買い物かごリストの中身をチェック
		for (BasketBean basketBean : basket) {
			// 削除対象の商品IDと一致する場合
			if (basketBean.getId() == id) {
				// 注文数が1の場合
				if (basketBean.getOrderNum() == 1) {
					basket.remove(basketBean);
					break;
				} else { // 注文数が2個以上ある場合
					// 要素の注文数を現在の注文数-1する
					basketBean.setOrderNum(basketBean.getOrderNum() - 1);
					break;
				}
			}
		}

		// かごの中身が何もない場合
		if (basket.size() == 0) {
			// セッションの削除
			session.removeAttribute("basketBeans");
		} else { // かごに他の商品がある場合
			// 該当商品削除後の買い物かごを、セッションに保存
			session.setAttribute("basketBeans", basket);
		}

		updateBasketSummary(session);

		// 買い物かごリストにリダイレクト
		return "redirect:/client/basket/list";
	}

	/**
	 * 買い物かごの商品を全削除するメソッド
	 * 
	 * @param session セッション情報
	 * @redirect "client/basket/list" 買い物かご表示にリダイレクト
	 */
	@RequestMapping(path = "/client/basket/allDelete", method = RequestMethod.POST)
	public String basketAllDelete() {
		// セッションの破棄
		session.removeAttribute("basketBeans");

		updateBasketSummary(session);

		// 買い物かごリストにリダイレクト
		return "redirect:/client/basket/list";
	}

	/**
	 * 買い物かご内の合計点数と合計金額を計算し、セッションに保存する
	 *
	 * @param session セッション情報
	 */
	private void updateBasketSummary(HttpSession session) {
		List<BasketBean> basket = (List<BasketBean>) session.getAttribute("basketBeans");
		int totalCount = 0;
		int totalPrice = 0;

		if (basket != null && !basket.isEmpty()) {
			for (BasketBean bean : basket) {
				Item item = itemRepository.findByIdAndDeleteFlag(bean.getId(), jp.co.sss.shop.util.Constant.NOT_DELETED);
				if (item != null) {
					totalCount += bean.getOrderNum();
					totalPrice += item.getPrice() * bean.getOrderNum();
				}
			}
		}

		session.setAttribute("basketTotalCount", totalCount);
		session.setAttribute("basketTotalPrice", totalPrice);
	}
}