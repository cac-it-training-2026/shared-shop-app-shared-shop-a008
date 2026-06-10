package jp.co.sss.shop.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jp.co.sss.shop.entity.Item;

/**
 * itemsテーブル用リポジトリ
 *
 * @author System Shared
 */
@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {

	/**
	 * 商品情報を登録日付順に取得 管理者機能で利用
	 * @param deleteFlag 削除フラグ
	 * @param pageable ページング情報
	 * @return 商品エンティティのページオブジェクト
	 */
	@Query("SELECT i FROM Item i INNER JOIN i.category c WHERE i.deleteFlag =:deleteFlag ORDER BY i.insertDate DESC,i.id DESC")
	Page<Item> findByDeleteFlagOrderByInsertDateDescPage(
			@Param(value = "deleteFlag") int deleteFlag, Pageable pageable);

	/**
	 * 商品IDと削除フラグを条件に検索（管理者,商品詳細機能で利用）
	 * @param id 商品ID
	 * @param deleteFlag 削除フラグ
	 * @return 商品エンティティ
	 */
	public Item findByIdAndDeleteFlag(Integer id, int deleteFlag);

	/**
	 * 商品名と削除フラグを条件に検索 (ItemValidatorで利用)
	 * @param name 商品名
	 * @param notDeleted 削除フラグ
	 * @return 商品エンティティ
	 */
	public Item findByNameAndDeleteFlag(String name, int notDeleted);

	/**
	 * 新着順の商品一覧
	 * @param id 商品ID
	 * @return 商品エンティティのリスト
	 */
	List<Item> findAllByOrderByIdDesc();

	/**売れ筋順（注文回数が多い順）、左外部結合で注文情報がある商品だけでなく、全商品を参照
	 * @param deleteFlag 削除フラグ
	 * @return 商品エンティティのリスト
	 */
	@Query("SELECT i FROM Item i LEFT JOIN i.orderItemList oi WHERE i.deleteFlag = :deleteFlag GROUP BY i ORDER BY COUNT(oi.id) DESC, i.id ASC")
	List<Item> findAllByHotSellItems(@Param("deleteFlag") int deleteFlag);

	/**
	 * カテゴリ検索状態の新着順の商品の一覧
	 * @param categoryId カテゴリID
	 * @param deleteFlag 削除フラグ
	 * @return 商品エンティティのリスト
	 */
	List<Item> findByCategoryIdAndDeleteFlagOrderByIdDesc(Integer categoryId, int deleteFlag);

	/**
	 * カテゴリ検索状態の売れ筋順の商品一覧
	 * @param categoryId カテゴリID
	 * @param deleteFlag 削除フラグ
	 * @return 商品エンティティのリスト
	 */
	@Query("SELECT i FROM Item i LEFT JOIN i.orderItemList oi WHERE i.deleteFlag = :deleteFlag AND i.category.id = :categoryId GROUP BY i ORDER BY COUNT(oi.id) DESC, i.id ASC")
	List<Item> findHotSellItemsByCategory(@Param("categoryId") Integer categoryId, @Param("deleteFlag") int deleteFlag);

	/**
	 * 売れ筋順（注文回数が多い順）、内部結合で注文情報がある商品のみ参照
	 * @param deleteFlag 削除フラグ
	 * @return 商品エンティティのリスト
	 */
	@Query("SELECT i FROM Item i JOIN i.orderItemList oi WHERE i.deleteFlag = :deleteFlag GROUP BY i ORDER BY COUNT(oi.id) DESC, i.id ASC")
	List<Item> findByHotSellItems(@Param("deleteFlag") int deleteFlag);
}
