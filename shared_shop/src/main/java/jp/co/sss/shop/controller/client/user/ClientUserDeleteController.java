package jp.co.sss.shop.controller.client.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;
import jp.co.sss.shop.bean.UserBean;

@Controller//削除確認コントローラ
public class ClientUserDeleteController {

    /**
     * セッション情報
     */
    @Autowired
    HttpSession session;

    @RequestMapping(path = "/client/user/delete/check")
    public String useDeleteCheck(Model model) {
    	
    	//セッションからログイン中の会員情報を取得
    	UserBean userBean=(UserBean)session.getAttribute("user");
    			
    	//セッション情報がない場合
    	if(userBean==null) {
    				return"redirect:/syserror";
    			}
    	//会員情報を画面に渡す
    	model.addAttribute("userBean",userBean);
    	//削除確認画面表示
    	return"/client/user/delete/check";
    }
}
