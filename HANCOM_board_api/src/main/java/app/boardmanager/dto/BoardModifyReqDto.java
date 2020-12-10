package app.boardmanager.dto;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;

public class BoardModifyReqDto implements Serializable{
	private static final long serialVersionUID = -5447464762567997494L;

	@NotBlank(message = "{VALID.BOARD.SEQ.NOT_EMPTY}")
	private String seq;
	@NotBlank(message = "{VALID.BOARD.SUBJECT.NOT_EMPTY}")
	private String subject;
	@NotBlank(message = "{VALID.BOARD.CONTENT.NOT_EMPTY}")
	private String content;
	@NotBlank(message = "{VALID.BOARD.REGNAME.NOT_EMPTY}")
	private String regName;

	public BoardModifyReqDto() {}
	
	public String getSeq() {
		return seq;
	}

	public void setSeq(String seq) {
		this.seq = seq;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getRegName() {
		return regName;
	}

	public void setRegName(String regName) {
		this.regName = regName;
	}

	

}
