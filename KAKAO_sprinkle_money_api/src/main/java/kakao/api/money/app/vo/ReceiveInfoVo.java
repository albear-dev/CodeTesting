package kakao.api.money.app.vo;

import kakao.api.money.app.dto.CommonDto;

public class ReceiveInfoVo extends CommonDto{
	private static final long serialVersionUID = 1L;
	private String userId;
	private int amount;
	private long time;
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public int getAmount() {
		return amount;
	}
	public void setAmount(int amount) {
		this.amount = amount;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	
	@Override
	public String toString() {
		return "ReceiveInfoVo [userId=" + userId + ", amount=" + amount + ", time=" + time + "]";
	}
	
	public static String getReceiveInfoKey(String xRoomId, String xRequestToken) {
		return "ReceiveInfo:"+xRoomId+":"+xRequestToken;
	}
	
}
