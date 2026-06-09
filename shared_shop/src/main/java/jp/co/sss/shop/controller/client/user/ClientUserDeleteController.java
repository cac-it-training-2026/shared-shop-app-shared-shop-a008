package jp.co.sss.shop.controller.client.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;
import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.entity.User;
import jp.co.sss.shop.repository.UserRepository;
import jp.co.sss.shop.util.Constant;

	//削除確認コントローラ
	@Controller
	public class ClientUserDeleteController {

    /**
     * セッション情報
     */
    @Autowired
    HttpSession session;

    @RequestMapping(path = "/client/user/delete/check")
    public String useDeleteCheck(Model model) {
    	
    UserBean userBean = (UserBean) session.getAttribute("user");
    	
    	//セッション情報がない場合
    	if(userBean==null) {
    				return"redirect:/syserror";
    			}
    	//会員情報を画面に渡す
    	model.addAttribute("userForm",userBean);
    	//削除確認画面表示
    	return"client/user/delete_check";
    }

	@Autowired
	UserRepository userRepository;
	
	@RequestMapping(path = "/client/user/delete/complete")
	public String useDeleteComplete() {


	    // セッションからログインユーザー取得
	    UserBean userBean = (UserBean) session.getAttribute("user");

	    if (userBean == null) {
	        return "redirect:/syserror";
	    }

	    // DBから会員情報取得
	    User user = userRepository.findByIdAndDeleteFlag(
	            userBean.getId(), Constant.NOT_DELETED);

	  
	    
	    if (user == null) {
	        return "redirect:/syserror";
	    }

	    // 論理削除
	    user.setDeleteFlag(Constant.DELETED);

	    // 保存
	    userRepository.save(user);

	    // ログイン情報削除
	    session.removeAttribute("user");

	    // 完了画面へ
	    return "client/user/delete_complete";
	}
}