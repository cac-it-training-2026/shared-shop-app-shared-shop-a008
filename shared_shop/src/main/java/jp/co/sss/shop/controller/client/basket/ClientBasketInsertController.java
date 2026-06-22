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
import jp.co.sss.shop.bean.BasketItemBean;
import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.entity.Item;
import jp.co.sss.shop.form.BasketForm;
import jp.co.sss.shop.repository.ItemRepository;
import jp.co.sss.shop.util.Constant;

/**
 * 買い物かごの基本クラス
 * 
 * @author 諸星愛実
 */
@Controller
public class ClientBasketInsertController {

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
	 * @return "client/basket/list" 買い物かごの内容表示
	 */
	@RequestMapping(path = "/client/basket/list", method = RequestMethod.GET)
	public String basketList(Model model) {

		// ログインユーザーの取得
		UserBean loginUser = (UserBean) session.getAttribute("user");
		// ログインユーザーがnullの場合
		if (loginUser == null) {
			return "redirect:/login"; // ログイン画面にリダイレクト
		}

		// 買い物かご情報を取得
		BasketBean basket = (BasketBean) session.getAttribute("basketBean");

		// 在庫不足の場合のリストを生成
		List<String> itemNameListLessThan = new ArrayList<>();
		// 在庫が無い場合のリストを生成
		List<String> itemNameListZero = new ArrayList<>();

		// 在庫切れの際に、買い物かごから削除するためのリストを生成
		List<BasketItemBean> removeList = new ArrayList<>();

		// 買い物かご情報がある場合
		if (basket != null) {
			// 拡張for文で買い物かご内の各商品をチェック
			for (BasketItemBean basketItemBean : basket.getBasketItemBeanList()) {
				// 該当商品のエンティティオブジェクトを生成
				Item item = itemRepository.getReferenceById(basketItemBean.getId());

				// 在庫が無い場合
				if (item.getStock() == 0) {
					// 在庫なしリストに追加
					itemNameListZero.add(basketItemBean.getName());
					// 削除用リストに追加
					removeList.add(basketItemBean);
				} else if (basketItemBean.getOrderNum() > item.getStock()) { // 買い物かごの数量が在庫数より多い場合
					// 在庫不足リストに追加
					itemNameListLessThan.add(basketItemBean.getName());
					// 現在の在庫数まで減らす
					basketItemBean.setOrderNum(item.getStock());
				}

				// 表示用在庫数を最新化
				basketItemBean.setStock(item.getStock());
			}

			// 在庫切れのものを買い物かごから削除
			basket.getBasketItemBeanList().removeAll(removeList);

			// セッションに保存
			if (basket.getBasketItemBeanList().isEmpty()) {
				session.removeAttribute("basketBean");
			} else {
				session.setAttribute("basketBean", basket);
			}
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
	 * @param basketForm 買い物かご入力フォーム
	 * @redirect "client/basket/list" 買い物かご表示にリダイレクト
	 */
	@RequestMapping(path = "/client/basket/add", method = RequestMethod.POST)
	public String basketAdd(BasketForm basketForm) {
		// 買い物かご情報を取得
		BasketBean basket = (BasketBean) session.getAttribute("basketBean");

		// 買い物かご情報が存在しない場合
		if (basket == null) {
			// 新しい買い物かご情報を生成
			basket = new BasketBean();
		}

		// getReferenceByIdで商品情報を取得
		Item item = itemRepository.getReferenceById(basketForm.getId());

		// 買い物かごに入れる商品情報を生成
		BasketItemBean itemBean = new BasketItemBean();
		itemBean.setId(item.getId());
		itemBean.setName(item.getName());
		itemBean.setPrice(item.getPrice());
		itemBean.setStock(item.getStock());
		itemBean.setOrderNum(basketForm.getOrderNum());

		// 買い物かごに追加
		basket.add(itemBean);

		// セッションスコープに保存
		session.setAttribute("basketBean", basket);

		updateBasketSummary(session);

		// 買い物かごリストにリダイレクト
		return "redirect:/client/basket/list";
	}

	/**
	 * 買い物かごの商品を1つ減らす（または削除する）メソッド
	 * 
	 * @param id 削除する商品のID
	 * @redirect "client/basket/list" 買い物かご表示にリダイレクト
	 */
	@RequestMapping(path = "/client/basket/delete", method = RequestMethod.POST)
	public String basketDelete(Integer id) {
		// 買い物かご情報を取得
		BasketBean basket = (BasketBean) session.getAttribute("basketBean");

		if (basket != null) {
			// 指定した商品を減らす/削除する
			basket.delete(id);

			// かごの中身が何もない場合
			if (basket.getBasketItemBeanList().isEmpty()) {
				// セッションの削除
				session.removeAttribute("basketBean");
			} else {
				// 更新後の買い物かごをセッションに保存
				session.setAttribute("basketBean", basket);
			}
		}

		updateBasketSummary(session);

		// 買い物かごリストにリダイレクト
		return "redirect:/client/basket/list";
	}

	/**
	 * 買い物かごの商品を全削除するメソッド
	 * 
	 * @redirect "client/basket/list" 買い物かご表示にリダイレクト
	 */
	@RequestMapping(path = "/client/basket/allDelete", method = RequestMethod.POST)
	public String basketAllDelete() {
		// セッションの破棄
		session.removeAttribute("basketBean");

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
		BasketBean basket = (BasketBean) session.getAttribute("basketBean");
		int totalCount = 0;
		int totalPrice = 0;

		if (basket != null && !basket.getBasketItemBeanList().isEmpty()) {
			for (BasketItemBean bean : basket.getBasketItemBeanList()) {
				Item item = itemRepository.findByIdAndDeleteFlag(bean.getId(), Constant.NOT_DELETED);
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
