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

import jakarta.servlet.http.HttpSession;
import jp.co.sss.shop.bean.OrderBean;
import jp.co.sss.shop.bean.OrderItemBean;
import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.entity.Order;
import jp.co.sss.shop.entity.OrderItem;
import jp.co.sss.shop.repository.OrderRepository;
import jp.co.sss.shop.service.BeanTools;

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

    	//注文情報を取得
        Order order = orderRepository.findById(id).orElse(null);

        //注文情報が存在しない場合
        if (order == null) {
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
}