package jp.co.sss.shop.controller.client.basket;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpSession;
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
	 * @return "client/basket/list.html"
	 */
	@RequestMapping(path = "/client/basket/list", method = RequestMethod.GET)
	public String basketList(HttpSession session) {
		session.setAttribute("basketBeans", session.getAttribute("basketBeans"));
		// templates/client/basket/list.htmlに遷移
		return "client/basket/list";
	}

	/**
	 * 買い物かごに商品追加をするメソッド
	 * 
	 * @param session
	 * @param id
	 * @return
	 */
	@RequestMapping(path = "/client/basket/add", method = RequestMethod.POST)
	public String basketAdd(HttpSession session, Integer id) {
		// 受け渡されたIDの商品をセッションに追加

		return "redirect:/";
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