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

	/**
	 * 買い物かご内の商品一覧を表示するメソッド
	 * 
	 * @param session
	 * @return "client/basket/list.html" 買い物かごの内容表示
	 */
	@RequestMapping(path = "/client/basket/list", method = RequestMethod.GET)
	public String basketList(HttpSession session, Model model) {
		// 買い物かごリストを取得
		List<BasketBean> basket = (List<BasketBean>) session.getAttribute("basketBeans");

		// 在庫不足の場合のリストを生成
		List<String> itemNameListLessThan = new ArrayList<>();
		// 在庫が無い場合のリストを生成
		List<String> itemNameListZero = new ArrayList<>();

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
				} else if (item.getStock() < basketBean.getOrderNum()) { // 買い物かごの数量が在庫数より多い場合
					// 在庫不足リストに追加
					itemNameListLessThan.add(basketBean.getName());
				}
			}
		}

		// リクエストスコープに保存
		model.addAttribute("itemNameListLessThan", itemNameListLessThan);
		model.addAttribute("itemNameListZero", itemNameListZero);

		// templates/client/basket/list.htmlに遷移
		return "client/basket/list";

	}

	/**
	 * 買い物かごに商品追加をするメソッド
	 * 
	 * @param session
	 * @param id
	 * @redirect "client/basket/list" 買い物かご表示にリダイレクト
	 */
	@RequestMapping(path = "/client/basket/add", method = RequestMethod.POST)
	public String basketAdd(HttpSession session, Integer id) {
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

		// 在庫上限フラグ
		boolean stockOver = false;

		// 拡張for文で買い物かごリストの中身をチェック
		for (BasketBean existBasketBeans : basket) {
			// 既存買い物かごの商品IDと、選択商品IDが同じ場合
			if (existBasketBeans.getId() == item.getId()) {
				if (existBasketBeans.getOrderNum() > item.getStock()) {
					stockOver = true;
					break;
				}
				// 商品注文個数を現在の個数＋1する
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

		// 買い物かごリストにリダイレクト
		return "redirect:/client/basket/list";
	}

	/**
	 * 買い物かごの商品を削除するメソッド
	 * 
	 * @param session
	 * @param id
	 * @redirect "client/basket/list" 買い物かご表示にリダイレクト
	 */
	@RequestMapping(path = "/client/basket/delete", method = RequestMethod.POST)
	public String basketDelete(HttpSession session, Integer id) {
		return "redirect:/client/basket/list";
	}

	/**
	 * 買い物かごの商品を全削除するメソッド
	 * 
	 * @param session
	 * @param id
	 * @redirect "client/basket/list" 買い物かご表示にリダイレクト
	 */
	@RequestMapping(path = "/client/baket/allDelete", method = RequestMethod.POST)
	public String basketAllDelete(HttpSession session, Integer id) {
		return "redirect:/client/basket/list";
	}
}