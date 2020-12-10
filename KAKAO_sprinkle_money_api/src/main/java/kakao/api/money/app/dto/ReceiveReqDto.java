package kakao.api.money.app.dto;

import javax.validation.constraints.NotEmpty;

import org.springframework.stereotype.Component;

@Component
public class ReceiveReqDto extends CommonDto{
	private static final long serialVersionUID = 1L;
	@NotEmpty(message = "{VALID.RECEIVE.TOKEN.NOT_EMPTY}")
	private String token;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	@Override
	public String toString() {
		return "ReceiveReqDto [token=" + token + "]";
	}
}