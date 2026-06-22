package jp.co.sss.shop.filter;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.entity.Order;
import jp.co.sss.shop.repository.OrderRepository;

/**
 * 認可フィルター
 */
@Component
public class AuthorizationFilter extends HttpFilter {

    @Autowired
    OrderRepository orderRepository;

    @Override
    public void init(jakarta.servlet.FilterConfig filterConfig) throws ServletException {
        SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this, filterConfig.getServletContext());
    }

    @Override
    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        String uri = request.getRequestURI();
        String contextPath = request.getContextPath();
        String path = uri.substring(contextPath.length());

        HttpSession session = request.getSession();
        UserBean user = (UserBean) session.getAttribute("user");

        // 管理画面アクセス制御
        if (path.startsWith("/admin/")) {
            if (user == null) {
                response.sendRedirect(contextPath + "/login");
                return;
            }
            if (!"ADMIN".equals(user.getRole())) {
                response.sendRedirect(contextPath + "/403");
                return;
            }
        }

        // 一般ユーザーの本人確認制御
        if (user != null && "USER".equals(user.getRole())) {
            // 会員情報アクセス制御
            if (path.startsWith("/client/user/detail") ||
                path.startsWith("/client/user/update") ||
                path.startsWith("/client/user/delete")) {
                // これらのURLは現在の仕様ではセッションのIDを使用しているため、
                // 他人のIDを指定してアクセスするパス（/client/user/detail/1 など）は存在しない。
                // ただし、将来的にIDが含まれるようになった場合の備えとして検討が必要。
                // 現状のコントローラ実装では session.getAttribute("user") の ID を使用している。
            }

            // 注文情報アクセス制御
            if (path.startsWith("/client/order/detail/") ||
                path.startsWith("/client/order/reorder/") ||
                path.startsWith("/client/order/cancel/")) {

                try {
                    String[] parts = path.split("/");
                    Integer orderId = Integer.parseInt(parts[parts.length - 1]);
                    Order order = orderRepository.findById(orderId).orElse(null);
                    if (order != null && !order.getUser().getId().equals(user.getId())) {
                        response.sendRedirect(contextPath + "/403");
                        return;
                    }
                } catch (NumberFormatException e) {
                    // IDが数値でない場合は各コントローラのバリデーションに任せるか、エラーへ
                }
            }
        }

        chain.doFilter(request, response);
    }
}
