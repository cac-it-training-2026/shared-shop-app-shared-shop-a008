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

@Controller
public class ClientUserShowController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    HttpSession session;

    @RequestMapping(path = "/client/user/detail")
    public String showUser(Model model) {

        UserBean loginUser = (UserBean) session.getAttribute("user");

        User user = userRepository.findByIdAndDeleteFlag(
                loginUser.getId(), Constant.NOT_DELETED);

        if (user == null) {
            return "redirect:/syserror";
        }

        UserBean userBean = new UserBean();
        BeanUtils.copyProperties(user, userBean);

        model.addAttribute("userBean", userBean);

        return "client/user/detail";
    }
}