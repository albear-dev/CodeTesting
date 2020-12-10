package analyzer.app.dto;

public class AnalyzeResDto {
	
	private String cut;
	private String remainder;
	
	public String getCut() {
		return cut;
	}
	public AnalyzeResDto setCut(String cut) {
		this.cut = cut;
		return this;
	}
	public String getRemainder() {
		return remainder;
	}
	public AnalyzeResDto setRemainder(String remainder) {
		this.remainder = remainder;
		return this;
	}
	
	@Override
	public String toString() {
		return "AnalyzeResDto [cut=" + cut + ", remainder=" + remainder + "]";
	}
}
