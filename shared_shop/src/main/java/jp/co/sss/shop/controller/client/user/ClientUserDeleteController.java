package jp.co.sss.shop.controller.client.user;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpSession;
import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.entity.User;
import jp.co.sss.shop.repository.UserRepository;
import jp.co.sss.shop.util.Constant;


/**
 * 

削除確認コントローラクラス


@author 富田
*/

	@Controller
	public class ClientUserDeleteController {

    /**
     * セッション情報
     */
    @Autowired
    HttpSession session;
    
    /**
     * 会員情報リポジトリ
     */
	@Autowired
	UserRepository userRepository;
	
	
	/**
	 * 退会確認画面表示への入口
	 *
	 * @return "redirect:/client/user/delete/check" 退会確認画面表示処理
	 */
	@RequestMapping(path = "/client/user/delete/check", method = RequestMethod.POST)
	public String userDeleteCheckInit() {

		UserBean loginUser = (UserBean) session.getAttribute("user");

	 	if (loginUser == null) {
	        return "redirect:/login";
	 	}
	    // 退会確認画面表示処理へリダイレクト
	    return "redirect:/client/user/delete/check";
		}

	/**
	 * 退会確認画面に実際に表示する処理
	 *
	 * @param model Viewとの値受渡し
	 * @return "client/user/delete_check" 退会確認画面
	 */
    @RequestMapping(path = "/client/user/delete/check", method = RequestMethod.GET)
    public String userDeleteCheck(Model model) {
    
    	// セッションからログインユーザー取得
    	UserBean loginUser = (UserBean) session.getAttribute("user");
    	
    	//セッション情報がない場合
    	if(loginUser == null) {
    		return"redirect:/syserror";
    	}
    	
    	User user = userRepository.findByIdAndDeleteFlag(loginUser.getId(),Constant.NOT_DELETED);
   
    	// 対象が存在しない場合
        if (user == null) {
            return "redirect:/syserror";
        }
        
        UserBean userBean = new UserBean ();
    	BeanUtils.copyProperties(user, userBean);
    	
    	//会員情報を画面に渡す
    	model.addAttribute("userForm",userBean);
    	
    	//削除確認画面表示
    	return"client/user/delete_check";
    }
    
    /**
     * 退会確認・完了画面表示処理
     *
     * @param model Viewとの値受渡し
     * @return "client/user/delete_check" 退会確認画面
     */
	
	@RequestMapping(path = "/client/user/delete/complete", method = RequestMethod.POST)
	public String userDeleteComplete() {


	    // セッションからログインユーザー取得
	    UserBean userBean = (UserBean) session.getAttribute("user");

	    if (userBean == null) {
	        return "redirect:/login";
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
	    
	    // 買い物かご情報を削除
	    session.removeAttribute("basketBean");
	    // カート合計情報を削除
	    session.removeAttribute("basketTotalCount");
	    session.removeAttribute("basketTotalPrice");

	    // ログイン情報削除
	    session.removeAttribute("user");

	    // 完了画面表示処理へリダイレクト
	    return "redirect:/client/user/delete/complete";
	}
	
	@RequestMapping(path = "/client/user/delete/complete", method = RequestMethod.GET)
	public String userDeleteCompleteFinish() {

	    // 退会完了画面を表示
	    return "client/user/delete_complete";
	}
}