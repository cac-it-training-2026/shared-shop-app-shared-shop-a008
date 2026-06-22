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
import jp.co.sss.shop.util.JapaneseNormalizer;

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

		//DONE
		/*TODO 現在は全件表示を行っている
		 * これを売れ筋（注文回数が多い順）に改修する*/

		// 注文情報の商品情報を全件表示
		//		List<Item> itemList = itemRepository.findAll();

		// 注文がある商品情報のみを売れ筋で表示
		List<Item> itemList = itemRepository.findByHotSellItems(Constant.NOT_DELETED);

		// 注文がある商品情報がない場合
		if (itemList.isEmpty()) {

			// 表示順をViewに渡す（1：新着順）
			model.addAttribute("sortType", 1);

			// 新着順の商品情報を取得する
			itemList = itemRepository.findByDeleteFlagOrderByIdDesc(Constant.NOT_DELETED);

			if (itemList.isEmpty()) {
				model.addAttribute("sortType", 2);
			}
		}

		// エンティティ内の検索結果をJavaBeansにコピー
		List<ItemBean> itemBeanList = beanTools.copyEntityListToItemBeanList(itemList);

		// 商品情報をViewへ渡す
		model.addAttribute("items", itemBeanList);

		// カテゴリ情報をViewへ渡す（追加）
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
			@RequestParam(required = false) String keyword, Model model) {

		// Item型のリストの宣言
		List<Item> itemList;

		// 検索キーワードの有無を確認
		boolean hasKeyword = keyword != null && !keyword.isEmpty();
		// カテゴリ選択の有無を確認
		boolean hasCategory = categoryId != null && categoryId != 0;

		if (hasCategory) {
			// カテゴリの存在チェック
			if (categoryRepository.findByIdAndDeleteFlag(categoryId, Constant.NOT_DELETED) == null) {
				return "redirect:/syserror";
			}
		}

		// 検索キーワードの正規化（カタカナに統一）
		String normalizedKeyword = hasKeyword ? JapaneseNormalizer.normalize(keyword) : null;

		// 表示順による分岐
		if (sortType == 1) {
			// 新着順
			if (hasKeyword && hasCategory) {
				itemList = itemRepository.findByCategoryIdAndNameOrKanaContaining(categoryId, keyword,
						normalizedKeyword, Constant.NOT_DELETED);
			} else if (hasKeyword) {
				itemList = itemRepository.findByNameOrKanaContaining(keyword, normalizedKeyword, Constant.NOT_DELETED);
			} else if (hasCategory) {
				itemList = itemRepository.findByCategoryIdAndDeleteFlagOrderByIdDesc(categoryId, Constant.NOT_DELETED);
			} else {
				itemList = itemRepository.findByDeleteFlagOrderByIdDesc(Constant.NOT_DELETED);
			}
		} else if (sortType == 2) {
			// 売れ筋順
			if (hasKeyword && hasCategory) {
				itemList = itemRepository.findHotSellItemsByCategoryIdAndNameOrKanaContaining(categoryId, keyword,
						normalizedKeyword, Constant.NOT_DELETED);
			} else if (hasKeyword) {
				itemList = itemRepository.findHotSellItemsByNameOrKanaContaining(keyword, normalizedKeyword,
						Constant.NOT_DELETED);
			} else if (hasCategory) {
				itemList = itemRepository.findHotSellItemsByCategory(categoryId, Constant.NOT_DELETED);
			} else {
				itemList = itemRepository.findAllByHotSellItems(Constant.NOT_DELETED);
			}
		} else {
			return "redirect:/syserror";
		}

		// ItemListの値をitemBeanListにコピー
		List<ItemBean> itemBeanList = beanTools.copyEntityListToItemBeanList(itemList);

		// 商品情報をViewへ渡す
		model.addAttribute("items", itemBeanList);

		// 表示順をViewへ渡す
		model.addAttribute("sortType", sortType);

		// カテゴリIDをViewへ渡す
		model.addAttribute("categoryId", categoryId);

		// 検索キーワードをViewへ渡す
		model.addAttribute("keyword", keyword);

		// カテゴリ情報をViewへ渡す
		model.addAttribute("categories",
				categoryRepository.findByDeleteFlagOrderByInsertDateDescIdDesc(Constant.NOT_DELETED));

		return "client/item/list";
	}

}
