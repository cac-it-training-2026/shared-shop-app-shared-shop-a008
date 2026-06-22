package jp.co.sss.shop.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 買い物かご全体の情報を保持するクラス
 */
public class BasketBean implements Serializable {

	/**
	 * 商品リスト
	 */
	private List<BasketItemBean> basketItemBeanList = new ArrayList<>();

	/**
	 * 商品リストの取得
	 * @return 商品リスト
	 */
	public List<BasketItemBean> getBasketItemBeanList() {
		return basketItemBeanList;
	}

	/**
	 * 商品リストのセット
	 * @param basketItemBeanList 商品リスト
	 */
	public void setBasketItemBeanList(List<BasketItemBean> basketItemBeanList) {
		this.basketItemBeanList = basketItemBeanList;
	}

	/**
	 * 指定した商品IDを持つ商品の注文数を追加する。
	 * 同一商品がリストにある場合は加算し、ない場合は新規追加する。
	 * 
	 * @param itemBean 追加する商品情報
	 */
	public void add(BasketItemBean itemBean) {
		boolean exist = false;
		for (BasketItemBean bean : basketItemBeanList) {
			if (bean.getId().equals(itemBean.getId())) {
				bean.setOrderNum(bean.getOrderNum() + itemBean.getOrderNum());
				exist = true;
				break;
			}
		}
		if (!exist) {
			basketItemBeanList.add(itemBean);
		}
	}

	/**
	 * 指定した商品IDを持つ商品の注文数を減らす（または削除する）。
	 *
	 * @param id 削除する商品ID
	 */
	public void delete(Integer id) {
		for (int i = 0; i < basketItemBeanList.size(); i++) {
			BasketItemBean bean = basketItemBeanList.get(i);
			if (bean.getId().equals(id)) {
				if (bean.getOrderNum() > 1) {
					bean.setOrderNum(bean.getOrderNum() - 1);
				} else {
					basketItemBeanList.remove(i);
				}
				break;
			}
		}
	}

	/**
	 * 買い物かごを空にする。
	 */
	public void allDelete() {
		basketItemBeanList.clear();
	}

	/**
	 * 自身を複製する。
	 * @return 複製されたオブジェクト
	 */
	public BasketBean copy() {
		BasketBean newBean = new BasketBean();
		List<BasketItemBean> newList = new ArrayList<>();
		for (BasketItemBean item : this.basketItemBeanList) {
			newList.add(item.copy());
		}
		newBean.setBasketItemBeanList(newList);
		return newBean;
	}
}
