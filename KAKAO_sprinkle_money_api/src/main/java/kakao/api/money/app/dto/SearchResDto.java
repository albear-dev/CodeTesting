package kakao.api.money.app.dto;

import java.util.List;

import kakao.api.money.app.vo.ReceiveInfoVo;

public class SearchResDto {
	private String sprinkleTime;
	private int sprinkleAmount;
	private int totalReceiveAmout;
	private List<ReceiveInfoVo> receiveInfo;
	
	public String getSprinkleTime() {
		return sprinkleTime;
	}

	public void setSprinkleTime(String sprinkleTime) {
		this.sprinkleTime = sprinkleTime;
	}

	public int getSprinkleAmount() {
		return sprinkleAmount;
	}

	public void setSprinkleAmount(int sprinkleAmount) {
		this.sprinkleAmount = sprinkleAmount;
	}

	public int getTotalReceiveAmout() {
		return totalReceiveAmout;
	}

	public void setTotalReceiveAmout(int totalReceiveAmout) {
		this.totalReceiveAmout = totalReceiveAmout;
	}

	public List<ReceiveInfoVo> getReceiveInfo() {
		return receiveInfo;
	}

	public void setReceiveInfo(List<ReceiveInfoVo> receiveInfo) {
		this.receiveInfo = receiveInfo;
	}
	
	@Override
	public String toString() {
		return "SearchResDto [sprinkleTime=" + sprinkleTime + ", sprinkleAmount=" + sprinkleAmount
				+ ", totalReceiveAmout=" + totalReceiveAmout + ", receiveInfo=" + receiveInfo + "]";
	}

	public class SearchResListDto{
		private String userId;
		private int amount;
		private String time;
		
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
		public String getTime() {
			return time;
		}
		public void setTime(String time) {
			this.time = time;
		}
		@Override
		public String toString() {
			return "SearchResListDto [userId=" + userId + ", amount=" + amount + ", time=" + time + "]";
		}
	}
}
