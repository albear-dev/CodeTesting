package app.common.validator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * @author hyeonny.kim
 * 
 * Bean 검증에 Custom Validation 사항이 있어 추가 구현
 * PasswordValidator 에서 실제 정규식 검사 수행함
 */
@Target({ ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = { PasswordValidator.class })
public @interface PasswordCheck {
	String message() default "";
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};

}
