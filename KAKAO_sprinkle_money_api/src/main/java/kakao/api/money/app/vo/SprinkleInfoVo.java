package kakao.api.money.app.vo;

import kakao.api.money.app.dto.CommonDto;

public class SprinkleInfoVo extends CommonDto{
	private static final long serialVersionUID = 1L;
	
	/** 분배자 */
	private String sprinkleUser;
	/** 총 분배 금액 */
	private int sprinkleAmount;
	/** 분배인원 */
	private int sprinkleCount;
	/** 분배시각 */
	private long sprinkleTime;
	
	public String getSprinkleUser() {
		return sprinkleUser;
	}
	public void setSprinkleUser(String sprinkleUser) {
		this.sprinkleUser = sprinkleUser;
	}
	public int getSprinkleAmount() {
		return sprinkleAmount;
	}
	public void setSprinkleAmount(int sprinkleAmount) {
		this.sprinkleAmount = sprinkleAmount;
	}
	public int getSprinkleCount() {
		return sprinkleCount;
	}
	public void setSprinkleCount(int sprinkleCount) {
		this.sprinkleCount = sprinkleCount;
	}
	public long getSprinkleTime() {
		return sprinkleTime;
	}
	public void setSprinkleTime(long sprinkleTime) {
		this.sprinkleTime = sprinkleTime;
	}
	
	@Override
	public String toString() {
		return "SprinkleInfoVo [sprinkleUser=" + sprinkleUser + ", sprinkleAmount=" + sprinkleAmount
				+ ", sprinkleCount=" + sprinkleCount + ", sprinkleTime=" + sprinkleTime + "]";
	}
	
	public static String getSplinkleInfoKey(String xRoomId, String xRequestToken) {
		return "SprinkleInfo:"+xRoomId+":"+xRequestToken;
	}
	
	
	
}
