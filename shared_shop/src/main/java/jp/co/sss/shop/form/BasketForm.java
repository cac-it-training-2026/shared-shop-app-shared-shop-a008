package jp.co.sss.shop.form;

import java.io.Serializable;

/**
 * 買い物かごのフォーム
 */
public class BasketForm implements Serializable {

	/**
	 * 商品ID
	 */
	private Integer id;

	/**
	 * 注文数
	 */
	private Integer orderNum;

	/**
	 * 商品ID取得
	 * @return 商品ID
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * 商品IDのセット
	 * @param id 商品ID
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * 注文数の取得
	 * @return 注文数
	 */
	public Integer getOrderNum() {
		return orderNum;
	}

	/**
	 * 注文数のセット
	 * @param orderNum 注文数
	 */
	public void setOrderNum(Integer orderNum) {
		this.orderNum = orderNum;
	}
}
