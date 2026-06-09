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

/**

会員詳細表示機能のコントローラクラス


@author 富田
*/
	@Controller
	public class ClientUserShowController {
	
	/**
	会員情報リポジトリ
	*/
    @Autowired
    UserRepository userRepository;
    
    /**
    セッション情報
    */
    @Autowired
    HttpSession session;
    
    /**

    会員詳細画面表示処理


    @param model Viewとの値受渡し
    @return "client/user/detail" 会員詳細画面
    */
    @RequestMapping(path = "/client/user/detail")
    public String showUser(Model model) {

    	// セッションからログイン中の会員情報を取得
        UserBean loginUser = (UserBean) session.getAttribute("user");
     
        // 会員IDをもとに未削除の会員情報を取得
        User user = userRepository.findByIdAndDeleteFlag(
                loginUser.getId(), Constant.NOT_DELETED);

        //nullだったら/syserrorに遷移
        if (user == null) {
            return "redirect:/syserror";
        }

        // Userエンティティの情報をUserBeanにコピー
        UserBean userBean = new UserBean();
        BeanUtils.copyProperties(user, userBean);

        // 会員情報をリクエストスコープへ設定
        model.addAttribute("userBean", userBean);

        // 会員詳細画面を表示
        return "client/user/detail"; 
    }
}