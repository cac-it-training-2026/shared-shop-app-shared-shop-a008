package jp.co.sss.shop.controller.client.user;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;
import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.entity.User;
import jp.co.sss.shop.repository.UserRepository;
import jp.co.sss.shop.util.Constant;

@Controller//会員詳細表示のコントローラ
public class ClientUserShowController {

    @Autowired//意味：UserRepositoryを自動で用意してください
    UserRepository userRepository;

    @Autowired//ログインユーザを保持するセッション
    HttpSession session;

    @RequestMapping(path = "/client/user/detail")
    public String showUser(Model model) {

    	// セッションからログイン中の会員情報を取得
        UserBean loginUser = (UserBean) session.getAttribute("user");
     // 会員IDをもとに未削除の会員情報を取得
        User user = userRepository.findByIdAndDeleteFlag(
                loginUser.getId(), Constant.NOT_DELETED);

        if (user == null) {//nullだったら/syserrorに遷移
            return "redirect:/syserror";
        }

        UserBean userBean = new UserBean();// Userエンティティの情報をUserBeanにコピー
        BeanUtils.copyProperties(user, userBean);

        model.addAttribute("userBean", userBean);// 会員情報をリクエストスコープへ設定

        return "client/user/detail"; // 会員詳細画面を表示
    }
}