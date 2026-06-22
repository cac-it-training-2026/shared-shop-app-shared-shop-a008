package jp.co.sss.shop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * エラー画面表示用コントローラ
 */
@Controller
public class CustomErrorController {

    /**
     * 403エラー画面表示
     * @return "error/403"
     */
    @RequestMapping("/403")
    public String show403() {
        return "error/403";
    }
}
