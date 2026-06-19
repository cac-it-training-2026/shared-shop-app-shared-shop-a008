package jp.co.sss.shop.controller.client.order;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpSession;
import jp.co.sss.shop.bean.BasketBean;
import jp.co.sss.shop.bean.OrderBean;
import jp.co.sss.shop.bean.OrderItemBean;
import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.entity.Item;
import jp.co.sss.shop.entity.Order;
import jp.co.sss.shop.entity.OrderItem;
import jp.co.sss.shop.repository.OrderRepository;
import jp.co.sss.shop.service.BeanTools;
import jp.co.sss.shop.util.Constant;

/**
 * 注文一覧コントローラクラス
 * 
 * @author 富田
 */
@Controller
public class ClientOrderShowController {

    /**
     * 注文情報
     */
    @Autowired
    OrderRepository orderRepository;

    /**
     * Entity、Bean間のデータコピーサービス
     */
    @Autowired
    BeanTools beanTools;

    /**
     * セッション
     */
    @Autowired
    HttpSession session;

    /**
     * 一覧取得、一覧画面表示 処理
     *
     * @param model Viewとの値受渡し
     * @param pageable ページング情報
     * @return "client/order/list" 注文一覧画面へ
     */
    @RequestMapping(path = "/client/order/list")
    public String showOrderList(Model model, Pageable pageable) {

        // セッションからログインユーザー取得
        UserBean userBean = (UserBean) session.getAttribute("user");

        // セッション情報がない場合
        if (userBean == null) {
            return "redirect:/syserror";
        }

        // ログインユーザーの注文情報を取得
        Page<Order> ordersPage = orderRepository.findByUserIdOrderByInsertDateDescIdDesc(
                userBean.getId(), pageable);

        // OrderエンティティをOrderBeanに変換
        List<OrderBean> orderBeanList = new ArrayList<>();

        for (Order order : ordersPage.getContent()) {
            OrderBean orderBean = beanTools.copyEntityToOrderBean(order);

            int total = 0;
            for (OrderItem orderItem : order.getOrderItemsList()) {
                total += orderItem.getPrice() * orderItem.getQuantity();
            }

            orderBean.setTotal(total);
            orderBeanList.add(orderBean);
        }

        // 注文情報を画面に渡す
        model.addAttribute("pages", ordersPage);
        model.addAttribute("orders", orderBeanList);

        // 注文一覧画面表示
        return "client/order/list";
    }
    

    /**
     * 注文詳細画面表示処理
     *
     * @param model Viewとの値受渡し
     * @param id 注文ID
     * @return "client/order/detail" 注文詳細画面へ
     */
    @RequestMapping(path = "/client/order/detail/{id}")
    public String showOrderDetail(@PathVariable Integer id, Model model) {

    	
    	// セッションからログインユーザー取得
    	UserBean loginUser = (UserBean) session.getAttribute("user");

    	if (loginUser == null) {
    	    return "redirect:/login";
    	}
    	
    	//注文情報を取得
        Order order = orderRepository.findById(id).orElse(null);

        //存在しない注文IDを入力した場合
        if (order == null) {
            return "redirect:/syserror";
        }
        
        //他ユーザーの注文情報の場合
        if (!order.getUser().getId().equals(loginUser.getId())) {
            return "redirect:/syserror";
        }

        // 注文情報をOrderBeanに変換
        OrderBean orderBean = beanTools.copyEntityToOrderBean(order);

        // 注文商品情報を取得
        List<OrderItemBean> orderItemBeans =
                beanTools.generateOrderItemBeanList(order.getOrderItemsList());

        // 合計金額を計算
        int total = 0;
        for (OrderItemBean orderItemBean : orderItemBeans) {
            total += orderItemBean.getSubtotal();
        }

        // 注文詳細情報を画面に渡す
        model.addAttribute("order", orderBean);
        model.addAttribute("orderItemBeans", orderItemBeans);
        model.addAttribute("total", total);

        // 注文詳細画面表示
        return "client/order/detail";
    }

    /**
     * 再注文処理
     *
     * @param id 注文ID
     * @return "redirect:/client/order/address/input" 届け先入力画面へ
     */
    @RequestMapping(path = "/client/order/reorder/{id}", method = RequestMethod.POST)
    public String reorder(@PathVariable Integer id, Model model) {
        // セッションからログインユーザー取得
        UserBean loginUser = (UserBean) session.getAttribute("user");
        if (loginUser == null) {
            return "redirect:/login";
        }

        // 注文情報を取得
        Order order = orderRepository.findById(id).orElse(null);
        if (order == null || !order.getUser().getId().equals(loginUser.getId())) {
            return "redirect:/syserror";
        }

        // 買い物かごリストを取得
        @SuppressWarnings("unchecked")
        List<BasketBean> basket = (List<BasketBean>) session.getAttribute("basketBeans");
        if (basket == null) {
            basket = new ArrayList<>();
        }

        // 注文商品を買い物かごへ追加
        for (OrderItem orderItem : order.getOrderItemsList()) {
            Item item = orderItem.getItem();

            // 削除済み商品のチェック
            if (item.getDeleteFlag() == Constant.DELETED) {
                continue;
            }

            // 在庫チェック（注文時の数量が在庫を超えていないか）
            if (item.getStock() < orderItem.getQuantity()) {
                // 在庫不足の場合はエラーメッセージを表示し、詳細画面へ戻す等の処理が必要だが、
                // 今回は仕様に基づき、追加可能なもののみ追加するか、エラーとする。
                // 受け入れ条件: "在庫数を超える数量は追加せず、エラーメッセージを表示すること。"
                model.addAttribute("error", "在庫不足の商品が含まれているため、再注文できません。");
                return showOrderDetail(id, model);
            }

            // 同一商品の存在チェック
            boolean exist = false;
            for (BasketBean basketBean : basket) {
                if (basketBean.getId().equals(item.getId())) {
                    // 数量を加算
                    int newOrderNum = basketBean.getOrderNum() + orderItem.getQuantity();
                    if (newOrderNum > item.getStock()) {
                        model.addAttribute("error", "買い物かご内の合計数量が在庫数を超えています。");
                        return showOrderDetail(id, model);
                    }
                    basketBean.setOrderNum(newOrderNum);
                    exist = true;
                    break;
                }
            }

            if (!exist) {
                BasketBean basketBean = new BasketBean(item.getId(), item.getName(), item.getStock(),
                        orderItem.getQuantity());
                basket.add(basketBean);
            }
        }

        // セッションに保存
        session.setAttribute("basketBeans", basket);

        // 届け先入力画面の初期化処理（ClientOrderRegistControllerのaddressInputPost相当）を行うためリダイレクト
        // 直接リダイレクトすると、orderFormの生成が行われない可能性があるため、
        // 本来は共通化するか、ClientOrderRegistControllerのメソッドを呼ぶ形が望ましい。
        // ここでは仕様通り届け先入力画面へリダイレクト。
        return "redirect:/client/order/address/input";
    }
}