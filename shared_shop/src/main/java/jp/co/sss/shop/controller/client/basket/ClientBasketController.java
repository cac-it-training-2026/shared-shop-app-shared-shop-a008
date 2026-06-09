package jp.co.sss.shop.controller.client.basket;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpSession;
import jp.co.sss.shop.bean.BasketBean;
import jp.co.sss.shop.entity.Item;
import jp.co.sss.shop.repository.ItemRepository;

/**
 * 買い物かごの基本クラス
 */
@Controller
public class ClientBasketController {

	/** リポジトリのオブジェクトを生成 */
	ItemRepository itemRepository;

	/**
	 * 買い物かご内の商品一覧を表示するメソッド
	 * 
	 * @param session
	 * @return client/basket/list.html
	 */
	@RequestMapping(path = "/client/basket/list", method = RequestMethod.GET)
	public String basketList(HttpSession session) {
		// templates/client/basket/list.htmlに遷移
		return "client/basket/list";

		// 買い物かご表示時点で在庫数が買い物かごの数量を下回った場合の処理
		// コントローラーで在庫数取ってくる
		// if分岐で在庫不足だった場合、itemNameListLessThanでスコープ保存
		// 在庫がなかった場合、"itemNameListZero"でスコープ保存
	}

	/**
	 * 買い物かごに商品追加をするメソッド
	 * 
	 * @param session
	 * @param id
	 * @redirect client/basket/list
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

		// BasketBeanオブジェクトを生成
		BasketBean basketBean = new BasketBean();
		// 商品ID, 商品名, 在庫数をBeanにコピー
		basketBean.setId(item.getId());
		basketBean.setName(item.getName());
		basketBean.setStock(item.getStock());

		// 買い物かごリストに追加
		basket.add(basketBean);

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
	 * @return
	 */
	@RequestMapping(path = "/client/basket/delete", method = RequestMethod.POST)
	public String basketDelete(HttpSession session, Integer id) {
		return "redirect:/";
	}

	/**
	 * 買い物かごの商品を全削除するメソッド
	 * 
	 * @param session
	 * @param id
	 * @return
	 */
	@RequestMapping(path = "/client/baket/allDelete", method = RequestMethod.POST)
	public String basketAllDelete(HttpSession session, Integer id) {
		return "redirect:/";
	}
}