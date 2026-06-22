package jp.co.sss.shop.form;

import java.io.Serializable;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * 買い物かご追加用フォーム
 */
public class BasketForm implements Serializable {

	/**
	 * 商品ID
	 */
	@NotNull
	private Integer id;

	/**
	 * 注文個数
	 */
	@NotNull
	@Min(1)
	private Integer quantity;

	/**
	 * 商品IDの取得
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
	 * 注文個数の取得
	 * @return 注文個数
	 */
	public Integer getQuantity() {
		return quantity;
	}

	/**
	 * 注文個数のセット
	 * @param quantity 注文個数
	 */
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
}
