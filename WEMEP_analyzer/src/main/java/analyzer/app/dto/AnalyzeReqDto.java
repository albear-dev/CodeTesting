package analyzer.app.dto;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;

import org.hibernate.validator.constraints.URL;

public class AnalyzeReqDto extends CommonDto{
	private static final long serialVersionUID = 1L;
	
	@NotEmpty(message = "{VALID.URL.NOT_EMPTY}")
	@URL(message = "{VALID.URL.WRONG}")
	private String url;
	
	@Positive(message = "{VALID.UNIT.POSITIVE}")
	private int unit;
	
	@Positive(message = "{VALID.TYPE.POSITIVE}")
	@Min(value=1, message = "{VALID.TYPE.MIN_ERROR}")
	@Max(value=10000, message = "{VALID.TYPE.MAX_ERROR}")
	private int type;
	
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public int getUnit() {
		return unit;
	}
	public void setUnit(int unit) {
		this.unit = unit;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	@Override
	public String toString() {
		return "AnalyzeReqDto [url=" + url + ", unit=" + unit + ", type=" + type + "]";
	}
	
	
	public enum ANALYZE_TYPE{
		// main result type
		CONTAIN_HTML (1),
		WITHOUT_HTML (2)
	    ;
		private final int type;
		
		ANALYZE_TYPE(int type) {
	        this.type 	= type;
	    }

		public int getType() {
			return type;
		}
		
	}
	
	
}