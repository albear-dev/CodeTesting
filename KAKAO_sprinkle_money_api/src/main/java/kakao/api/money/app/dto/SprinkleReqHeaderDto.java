package kakao.api.money.app.dto;

import javax.validation.constraints.NotEmpty;

import org.springframework.stereotype.Component;

@Component
public class SprinkleReqHeaderDto {
	@NotEmpty(message = "{VALID.SPRINKLE.X-ROOM-ID.NOT_EMPTY}")
	private String xRoomId;
	@NotEmpty(message = "{VALID.SPRINKLE.X-USER-ID.NOT_EMPTY}")
	private String xUserId;
	
	public String getxRoomId() {
		return xRoomId;
	}
	public void setxRoomId(String xRoomId) {
		this.xRoomId = xRoomId;
	}
	public String getxUserId() {
		return xUserId;
	}
	public void setxUserId(String xUserId) {
		this.xUserId = xUserId;
	}
	
}
