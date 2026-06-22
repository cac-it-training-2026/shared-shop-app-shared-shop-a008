package jp.co.sss.shop.bean;

import java.io.Serializable;

/**
 * 買い物かご内の商品情報クラス
 */
public class BasketItemBean implements Serializable {

	/**
	 * 商品ID
	 */
	private Integer id;

	/**
	 * 商品名
	 */
	private String name;

	/**
	 * 商品価格
	 */
	private Integer price;

	/**
	 * 商品在庫数
	 */
	private Integer stock;

	/**
	 * 商品注文個数
	 */
	private Integer orderNum;

	/**
	 * コンストラクタ
	 */
	public BasketItemBean() {
	}

	/**
	 * コンストラクタ
	 *
	 * @param id  商品ID
	 * @param name  商品名
	 * @param price 商品価格
	 * @param stock 商品在庫数
	 * @param orderNum 注文数
	 */
	public BasketItemBean(Integer id, String name, Integer price, Integer stock, Integer orderNum) {
		this.id = id;
		this.name = name;
		this.price = price;
		this.stock = stock;
		this.orderNum = orderNum;
	}

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
	 * 商品名の取得
	 * @return 商品名
	 */
	public String getName() {
		return name;
	}

	/**
	 * 商品名のセット
	 * @param name 商品名
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 商品価格の取得
	 * @return 商品価格
	 */
	public Integer getPrice() {
		return price;
	}

	/**
	 * 商品価格のセット
	 * @param price 商品価格
	 */
	public void setPrice(Integer price) {
		this.price = price;
	}

	/**
	 * 商品の在庫数の取得
	 * @return 在庫数
	 */
	public Integer getStock() {
		return stock;
	}

	/**
	 * 商品の在庫数のセット
	 * @param stock 在庫数
	 */
	public void setStock(Integer stock) {
		this.stock = stock;
	}

	/**
	 * 買い物かごに入れている商品個数の取得
	 * @return 注文個数
	 */
	public Integer getOrderNum() {
		return orderNum;
	}

	/**
	 * 買い物かごに入れる商品個数のセット
	 * @param orderNum 注文個数
	 */
	public void setOrderNum(Integer orderNum) {
		this.orderNum = orderNum;
	}

	/**
	 * 自身を複製する。
	 * @return 複製されたオブジェクト
	 */
	public BasketItemBean copy() {
		return new BasketItemBean(this.id, this.name, this.price, this.stock, this.orderNum);
	}

}
