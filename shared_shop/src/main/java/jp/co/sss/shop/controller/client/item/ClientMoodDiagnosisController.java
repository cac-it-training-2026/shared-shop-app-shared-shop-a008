package jp.co.sss.shop.controller.client.item;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
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
		List<Item> itemList = new ArrayList<>();

		switch (mood) {
		case "relax":
			moodTitle = "ゆっくりしたい日のおすすめ";
			itemList = getRecommendedItemByCategoryName("リラックス");
			break;
		case "focus":
			moodTitle = "集中したい日のおすすめ";
			itemList = getRecommendedItemByCategoryName("書籍");
			break;
		case "energy":
			moodTitle = "元気を出したい日のおすすめ";
			itemList = getRecommendedItemByCategoryName("食料品");
			break;
		case "gift":
			moodTitle = "誰かに贈りたい日のおすすめ";
			itemList = itemRepository.findAllHotSellItems(Constant.NOT_DELETED, PageRequest.of(0, 1));
			if (itemList.isEmpty()) {
				itemList = itemRepository.findByDeleteFlagOrderByIdDesc(Constant.NOT_DELETED);
				if (!itemList.isEmpty()) {
					itemList = itemList.subList(0, 1);
				}
			}
			break;
		default:
			return "client/item/mood";
		}

		// ItemBeanに変換
		List<ItemBean> itemBeanList = beanTools.copyEntityListToItemBeanList(itemList);
		model.addAttribute("items", itemBeanList);
		model.addAttribute("moodTitle", moodTitle);
		model.addAttribute("mood", mood);

		return "client/item/mood";
	}

	/**
	 * カテゴリ名でおすすめ商品（注文数1位）を取得
	 *
	 * @param categoryName カテゴリ名
	 * @return 商品エンティティのリスト（1件または空）
	 */
	private List<Item> getRecommendedItemByCategoryName(String categoryName) {
		Category category = categoryRepository.findByNameAndDeleteFlag(categoryName, Constant.NOT_DELETED);
		List<Item> itemList;
		if (category != null) {
			itemList = itemRepository.findHotSellItemsByCategoryId(category.getId(), Constant.NOT_DELETED,
					PageRequest.of(0, 1));
			if (itemList.isEmpty()) {
				// カテゴリ内に注文履歴がない場合、カテゴリ内の商品を1件取得
				itemList = itemRepository.findByCategoryIdAndDeleteFlagOrderByIdDesc(category.getId(),
						Constant.NOT_DELETED);
				if (!itemList.isEmpty()) {
					itemList = itemList.subList(0, 1);
				}
			}
		} else {
			// カテゴリ自体が存在しない場合、全商品から1件取得
			itemList = itemRepository.findAllHotSellItems(Constant.NOT_DELETED, PageRequest.of(0, 1));
			if (itemList.isEmpty()) {
				itemList = itemRepository.findByDeleteFlagOrderByIdDesc(Constant.NOT_DELETED);
				if (!itemList.isEmpty()) {
					itemList = itemList.subList(0, 1);
				}
			}
		}
		return itemList;
	}
}
