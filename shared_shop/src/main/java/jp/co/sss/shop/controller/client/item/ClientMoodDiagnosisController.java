package jp.co.sss.shop.controller.client.item;

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
 * 気分で選ぶ商品診断のコントローラクラス
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
	 * 診断結果表示処理
	 *
	 * @param mood 気分
	 * @param model  Viewとの値受渡し
	 * @return "client/item/mood" 診断結果画面
	 */
	@RequestMapping(path = "/client/item/mood", method = { RequestMethod.GET, RequestMethod.POST })
	public String moodResult(String mood, Model model) {
		if (mood == null || mood.isEmpty()) {
			return "client/item/mood";
		}

		String moodTitle = "";
		int categoryIndex = -1;

		switch (mood) {
			case "relax":
				moodTitle = "ゆっくりしたい日のおすすめ";
				categoryIndex = 0;
				break;
			case "focus":
				moodTitle = "集中したい日のおすすめ";
				categoryIndex = 1;
				break;
			case "energy":
				moodTitle = "元気を出したい日のおすすめ";
				categoryIndex = 2;
				break;
			case "gift":
				moodTitle = "誰かに贈りたい日のおすすめ";
				categoryIndex = 3;
				break;
			default:
				return "client/item/mood";
		}

		// カテゴリ情報を取得
		List<Category> categories = categoryRepository.findByDeleteFlagOrderByInsertDateDescIdDesc(Constant.NOT_DELETED);

		// mood に基づいてカテゴリを選択
		if (categoryIndex >= 0 && categoryIndex < categories.size()) {
			Category selectedCategory = categories.get(categoryIndex);
			// 該当カテゴリの商品を取得
			List<Item> itemList = itemRepository.findByCategoryIdAndDeleteFlagOrderByIdDesc(selectedCategory.getId(),
					Constant.NOT_DELETED);
			// ItemBeanに変換
			List<ItemBean> itemBeanList = beanTools.copyEntityListToItemBeanList(itemList);
			model.addAttribute("items", itemBeanList);
		}

		model.addAttribute("moodTitle", moodTitle);
		model.addAttribute("mood", mood);

		return "client/item/mood";
	}
}
