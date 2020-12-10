package kakao.api.money.app.dto;

import javax.validation.constraints.Min;

import org.springframework.stereotype.Component;

@Component
public class SprinkleReqDto extends CommonDto{
	private static final long serialVersionUID = 1L;
	@Min(value=1, message = "{VALID.SPRINKLE.SPRINKLE_AMOUNT.NOT_EMPTY}")
	private int sprinkleAmount;
	@Min(value=1, message = "{VALID.SPRINKLE.SPRINKLE_COUNT.NOT_EMPTY}")
	private int sprinkleCount;
	
	/** 배분 금액 */
	public int getSprinkleAmount() {
		return sprinkleAmount;
	}
	/** 배분 금액 */
	public void setSprinkleAmount(int sprinkleAmount) {
		this.sprinkleAmount = sprinkleAmount;
	}
	/** 배분 인원 */
	public int getSprinkleCount() {
		return sprinkleCount;
	}
	/** 배분 인원 */
	public void setSprinkleCount(int sprinkleCount) {
		this.sprinkleCount = sprinkleCount;
	}
	
	@Override
	public String toString() {
		return "SprinkleReqDto [sprinkleAmount=" + sprinkleAmount + ", sprinkleCount=" + sprinkleCount + "]";
	}
}
