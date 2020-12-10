package app.common.validator;

import java.util.regex.Matcher;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author hyeonny.kim
 * 
 * Bean 검증에 Custom Validation 사항이 있어 추가 구현
 */
public class PasswordValidator implements ConstraintValidator<PasswordCheck, String> {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		
		
		// TODO 좀 더 간결하게 패턴 체크 변경 할것 
		String check1 	= "^(?=.*[a-z]).{1,}$";   						// 영문소
		String check2 	= "^(?=.*[A-Z]).{1,}$";   						// 영문대
		String check3 	= "^(?=.*[^a-zA-Z0-9]).{1,}$";  				// 특수문자
		String check4 	= "^(?=.*[0-9]).{2,}$";  						// 숫자

		if(StringUtils.isBlank(value)) {
			return false;
		}
		if(value.length() < 12) {
			return false;
		}
		
		Matcher match1 	= java.util.regex.Pattern.compile(check1).matcher(value);
		Matcher match2 	= java.util.regex.Pattern.compile(check2).matcher(value);
		Matcher match3 	= java.util.regex.Pattern.compile(check3).matcher(value);
		Matcher match4 	= java.util.regex.Pattern.compile(check4).matcher(value);
		
		int count = 0;
		if(match1.find()) {
			count++;
		}
		if(match2.find()) {
			count++;
		}
		if(match3.find()) {
			count++;
		}
		if(match4.find()) {
			count++;
		}
		if(count < 3) {
			return false;
		}
		
		return true;
	}
}
