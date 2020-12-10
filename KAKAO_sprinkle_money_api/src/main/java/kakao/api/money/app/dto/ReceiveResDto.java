package kakao.api.money.app.dto;

public class ReceiveResDto {
	private int amount;

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	@Override
	public String toString() {
		return "ReceiveResDto [amount=" + amount + "]";
	}

	
}
