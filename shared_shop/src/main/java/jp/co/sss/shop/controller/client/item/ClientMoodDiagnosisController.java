package jp.co.sss.shop.controller.client.item;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jp.co.sss.shop.bean.ItemBean;
import jp.co.sss.shop.entity.Category;
import jp.co.sss.shop.entity.Item;
import jp.co.sss.shop.repository.CategoryRepository;
import jp.co.sss.shop.repository.ItemRepository;
import jp.co.sss.shop.service.BeanTools;
import jp.co.sss.shop.util.Constant;

/**
 * 気分診断機能のコントローラクラス
 */
@Controller
public class ClientMoodDiagnosisController {

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
	 * 気分診断画面 表示処理
	 *
	 * @param mood  気分パラメータ
	 * @param model Viewとの値受渡し
	 * @return "client/item/mood" 気分診断画面
	 */
	@RequestMapping(path = "/client/item/mood", method = { RequestMethod.GET, RequestMethod.POST })
	public String showMoodDiagnosis(String mood, Model model) {

		List<Item> itemList = new ArrayList<>();

		if (mood != null) {
			switch (mood) {
			case "focus":
				// 「集中したい」：カテゴリ「書籍」の商品
				Category focusCategory = categoryRepository.findByNameAndDeleteFlag("書籍", Constant.NOT_DELETED);
				if (focusCategory != null) {
					itemList = itemRepository.findByCategoryIdAndDeleteFlagOrderByIdDesc(focusCategory.getId(), Constant.NOT_DELETED);
				}
				break;
			case "energy":
				// 「元気を出したい」：カテゴリ「食料品」の商品
				Category energyCategory = categoryRepository.findByNameAndDeleteFlag("食料品", Constant.NOT_DELETED);
				if (energyCategory != null) {
					itemList = itemRepository.findByCategoryIdAndDeleteFlagOrderByIdDesc(energyCategory.getId(), Constant.NOT_DELETED);
				}
				break;
			case "relax":
			case "gift":
				// 「ゆっくりしたい」「誰かに贈りたい」：売れ筋商品
				itemList = itemRepository.findAllByHotSellItems(Constant.NOT_DELETED);
				break;
			default:
				// 不正なパラメータの場合は何もしない（選択画面のみ）
				break;
			}
		}

		// エンティティ内の検索結果をJavaBeansにコピー
		List<ItemBean> itemBeanList = beanTools.copyEntityListToItemBeanList(itemList);

		// 商品情報をViewへ渡す
		model.addAttribute("items", itemBeanList);
		model.addAttribute("selectedMood", mood);

		return "client/item/mood";
	}
}
