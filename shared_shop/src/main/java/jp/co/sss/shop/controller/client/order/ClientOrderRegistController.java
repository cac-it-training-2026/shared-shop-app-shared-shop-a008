package jp.co.sss.shop.controller.client.order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jp.co.sss.shop.form.OrderForm;
import jp.co.sss.shop.repository.OrderRepository;

/**
 * 注文登録の基本クラス
 * 
 * @author 諸星愛実
 */
@Controller
public class ClientOrderRegistController {

	/** リポジトリのオブジェクトを生成 */
	@Autowired
	OrderRepository repository;

	/**
	 * @param session
	 * @redirect 
	 */
	@RequestMapping(path = "/client/order/address/input", method = RequestMethod.POST)
	public String addressInputPost(HttpSession session) {
		return "redirect:/client/order/address/input";
	}

	/**
	 * @param sessino
	 * @param model
	 * @return
	 */
	@RequestMapping("/client/order/address/input")
	public String addressInputGet(HttpSession sessino, Model model) {
		return "/client/order/address_input";
	}

	/**
	 * @param orderForm
	 * @param result
	 * @param session
	 * @redirect
	 */
	@RequestMapping(path = "/client/order/payment/input", method = RequestMethod.POST)
	public String paymentInputPost(@Valid @ModelAttribute OrderForm orderForm, BindingResult result,
			HttpSession session) {
		return "redirect:/client/order/payment/input";
	}

	/**
	 * @param session
	 * @param model
	 * @return
	 */
	@RequestMapping("/client/order/payment/input")
	public String paymentInputGet(HttpSession session, Model model) {
		return "/client/order/payment_input";
	}

	/**
	 * @redirect
	 */
	@RequestMapping(path = "/client/order/payment/back", method = RequestMethod.POST)
	public String paymentBack() {
		return "redirect:/client/order/address/input";
	}

	/**
	 * @param session
	 * @param model
	 * @redirect
	 */
	@RequestMapping(path = "/client/order/check", method = RequestMethod.POST)
	public String orderCheckPost(HttpSession session, Model model) {
		return "redirect:/client/order/check";
	}

	/**
	 * @param session
	 * @param model
	 * @return
	 */
	@RequestMapping("/client/order/check")
	public String orderCheckGet(HttpSession session, Model model) {
		return "/client/order/check";
	}

	/**
	 * @param session
	 * @redirect
	 */
	@RequestMapping(path = "/client/order/complete", method = RequestMethod.POST)
	public String orderCompletePost(HttpSession session) {
		return "redirect:/client/order/complete";
	}

	/**
	 * @param model
	 * @return
	 */
	@RequestMapping("/client/order/complete")
	public String orderCompleteGet(Model model) {
		return "/client/order/complete";
	}
}
