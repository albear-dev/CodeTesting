package app.boardmanager.dto;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;

public class BoardDeleteReqDto implements Serializable{
	
	private static final long serialVersionUID = 8812778954015295757L;
	
	@NotBlank(message = "{VALID.BOARD.SEQ.NOT_EMPTY}")
	private String seq;

	public BoardDeleteReqDto() {}
	
	public String getSeq() {
		return seq;
	}

	public void setSeq(String seq) {
		this.seq = seq;
	}
}
