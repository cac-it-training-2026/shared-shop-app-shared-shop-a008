package jp.co.sss.shop.controller.client.item;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import jp.co.sss.shop.bean.ItemBean;
import jp.co.sss.shop.entity.Item;
import jp.co.sss.shop.repository.CategoryRepository;
import jp.co.sss.shop.repository.ItemRepository;
import jp.co.sss.shop.service.BeanTools;
import jp.co.sss.shop.util.Constant;

/**
 * 商品管理 一覧表示機能(一般会員用)のコントローラクラス
 *
 * @author 石本稜
 */
@Controller
public class ClientItemShowController {
	/**
	 * 商品情報
	 */
	@Autowired
	ItemRepository itemRepository;

	/**
	 * カテゴリ情報
	 */
	@Autowired
	CategoryRepository categoryRepository;

	/**
	 * Entity、Form、Bean間のデータコピーサービス
	 */
	@Autowired
	BeanTools beanTools;

	/**
	 * トップ画面 表示処理
	 *
	 * @param model    Viewとの値受渡し
	 * @return "index" トップ画面
	 */
	@RequestMapping(path = "/", method = { RequestMethod.GET, RequestMethod.POST })
	public String index(Model model) {

		/*TODO 現在は全件表示を行っている
		 * これを売れ筋（注文回数が多い順）に改修する*/

		// 注文情報の商品情報を全件表示
		List<Item> itemList = itemRepository.findAll();

		// エンティティ内の検索結果をJavaBeansにコピー
		List<ItemBean> itemBeanList = beanTools.copyEntityListToItemBeanList(itemList);

		// 商品情報をViewへ渡す
		model.addAttribute("items", itemBeanList);

		//カテゴリ情報をViewへ渡す
		model.addAttribute("categories",
				categoryRepository.findByDeleteFlagOrderByInsertDateDescIdDesc(Constant.NOT_DELETED));

		return "index";
	}

	/**
	 * 詳細表示処理
	 *
	 * @param id      表示対象ID
	 * @param model   Viewとの値受渡し
	 * @return "client/item/detail" 詳細画面 表示
	 */
	@RequestMapping(path = "/client/item/detail/{id}")
	public String showItem(@PathVariable int id, Model model) {

		// 商品IDに該当する商品情報を取得する
		Item item = itemRepository.findByIdAndDeleteFlag(id, Constant.NOT_DELETED);
		if (item == null) {
			return "redirect:/syserror";
		}

		// Itemエンティティの各フィールドの値をItemBeanにコピー
		ItemBean itemBean = beanTools.copyEntityToItemBean(item);

		// 商品情報をViewへ渡す
		model.addAttribute("item", itemBean);

		return "client/item/detail";
	}

	/**
	 * @param sortType 表示順
	 * @param model Viewとの値受渡し
	 * @return "client/item/list" 商品一覧画面表示
	 */
	@RequestMapping(path = "/client/item/list/{sortType}")
	public String showItemList(@PathVariable Integer sortType, @RequestParam(required = false) Integer categoryId,
			Model model) {

		//Item型のリストの宣言
		List<Item> itemList;

		//		if (sortType == 1) {
		//
		//			//新着順の商品情報を取得する
		//			itemList = itemRepository.findAllByOrderByIdDesc();
		//
		//		} else {
		//
		//			//売れ筋順（未実装）の商品情報を取得する
		//			itemList = itemRepository.findAllByHotSellItems(Constant.NOT_DELETED);
		//
		//		}

		//カテゴリ検索されていないとき
		if (categoryId == null) {

			//新着順
			if (sortType == 1) {

				//新着順の商品情報を取得する
				itemList = itemRepository.findAllByOrderByIdDesc();

				//売れ筋順
			} else {

				//売れ筋順の商品情報を取得する
				itemList = itemRepository.findAllByHotSellItems(Constant.NOT_DELETED);

			}

			//カテゴリ検索されているとき
		} else {

			//新着順
			if (sortType == 1) {

				//検索されたカテゴリかつ新着順の商品情報を取得する
				itemList = itemRepository.findByCategoryIdAndDeleteFlagOrderByIdDesc(categoryId, Constant.NOT_DELETED);

				//売れ筋順
			} else {

				//検索されたカテゴリかつ売れ筋順の商品情報を取得する
				itemList = itemRepository.findHotSellItemsByCategory(categoryId, Constant.NOT_DELETED);

			}

		}

		//ItemListの値をitemBeanListにコピー
		List<ItemBean> itemBeanList = beanTools.copyEntityListToItemBeanList(itemList);

		//商品情報をViewへ渡す
		model.addAttribute("items", itemBeanList);

		//表示順をViewへ渡す
		model.addAttribute("sortType", sortType);

		//カテゴリIDをViewへ渡す
		model.addAttribute("categoryId", categoryId);

		//カテゴリ情報をViewへ渡す
		model.addAttribute("categories",
				categoryRepository.findByDeleteFlagOrderByInsertDateDescIdDesc(Constant.NOT_DELETED));

		return "client/item/list";
	}

}
