package kakao.api.money.app.common.messagesource;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * @author hyeonny.kim
 * 
 * 일부 Spring 에서 관리하지 않는 bean에서 Autowired 제약이 있을때 
 * resource bundle 정보를 가져오기 위해 구현함 
 *
 */
@Component
public class MessageSourceProvider implements ApplicationContextAware {
	private static ApplicationContext context;

	public static String getMessage(String message) {
//		Logger logger = LoggerFactory.getLogger(MessageSourceProvider.class);

		if (!StringUtils.isEmpty(message)) {
//			if(message.indexOf('{') == -1) {
//				return message;
//			}
			if (message.length() > 2 && message.indexOf('{') == 0 && message.lastIndexOf('}') == message.length() - 1) {
				message = message.substring(1, message.length() - 1);
			}
		}

		MessageSource messageSource = (ReloadableResourceBundleMessageSource) context.getBean("messageSource");
		Locale locale = LocaleContextHolder.getLocale();
		return messageSource.getMessage(message, null, locale);
	}

	@Override
	public void setApplicationContext(org.springframework.context.ApplicationContext applicationContext)
			throws BeansException {
		this.context = applicationContext;
	}
}
