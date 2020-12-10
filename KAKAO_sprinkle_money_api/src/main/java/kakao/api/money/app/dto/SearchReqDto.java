package kakao.api.money.app.dto;

import javax.validation.constraints.NotEmpty;

import org.springframework.stereotype.Component;

@Component
public class SearchReqDto extends CommonDto{
	private static final long serialVersionUID = 1L;
	@NotEmpty(message = "{VALID.SEARCH.TOKEN.NOT_EMPTY}")
	private String token;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	
}