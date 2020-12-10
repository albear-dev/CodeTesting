package app.boardmanager.dto;

import java.io.Serializable;

public class BoardDetailResDto implements Serializable{
	private static final long serialVersionUID = -5447464762567997494L;

	int seq;
	String subject;
	String content;
	String regDt;
	String regName;
	
	public BoardDetailResDto() {}

	public int getSeq() {
		return seq;
	}

	public void setSeq(int seq) {
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

	public String getRegDt() {
		return regDt;
	}

	public void setRegDt(String regDt) {
		this.regDt = regDt;
	}

	public String getRegName() {
		return regName;
	}

	public void setRegName(String regName) {
		this.regName = regName;
	}

	
	

}
