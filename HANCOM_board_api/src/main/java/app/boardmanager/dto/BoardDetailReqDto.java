package app.boardmanager.dto;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;

public class BoardDetailReqDto implements Serializable{
	private static final long serialVersionUID = -5447464762567997494L;

	@NotBlank(message = "{VALID.BOARD.SEQ.NOT_EMPTY}")
	private String seq;

	public BoardDetailReqDto() {}
	
	public String getSeq() {
		return seq;
	}

	public void setSeq(String seq) {
		this.seq = seq;
	}
	
	
	

}
