package jp.co.sss.shop.validator;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;

import jp.co.sss.shop.annotation.LoginCheck;
import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.entity.User;
import jp.co.sss.shop.repository.UserRepository;
import jp.co.sss.shop.util.Constant;

/**
 * ログインチェックの独自検証クラス
 *
 * @author System Shared
 */
public class LoginValidator implements ConstraintValidator<LoginCheck, Object> {
	private String email;
	private String password;

	@Autowired
	UserRepository userRepository;

	@Autowired
	HttpSession session;

	@Override
	public void initialize(LoginCheck annotation) {
		this.email = annotation.fieldEmail();
		this.password = annotation.fieldPassword();
	}

	@Override
	public boolean isValid(Object value, ConstraintValidatorContext context) {
		BeanWrapper beanWrapper = new BeanWrapperImpl(value);
		boolean isValidFlg = false;
		String emailProp = (String) beanWrapper.getPropertyValue(this.email);
		String passwordProp = (String) beanWrapper.getPropertyValue(this.password);

		User user = userRepository.findByEmailAndDeleteFlag(emailProp, Constant.NOT_DELETED);

		if (user != null) {
			// アカウントロック状態の確認
			java.sql.Timestamp lockDatetime = user.getLockDatetime();
			if (lockDatetime != null) {
				// 現在時刻とロック日時を比較（30分間ロック）
				long currentTime = System.currentTimeMillis();
				long lockTime = lockDatetime.getTime();
				long unlockTime = lockTime + (30 * 60 * 1000); // 30分後

				if (currentTime < unlockTime) {
					// ロック中の場合
					context.disableDefaultConstraintViolation();
					context.buildConstraintViolationWithTemplate("{msg.login.account.locked}")
							.addConstraintViolation();
					return false;
				}
			}

			if (passwordProp.equals(user.getPassword())) {
				isValidFlg = true;
			} else {
				// パスワード不一致
				isValidFlg = false;
			}
		} else {
			// ユーザが存在しない
			isValidFlg = false;
		}
		return isValidFlg;
	}
}
