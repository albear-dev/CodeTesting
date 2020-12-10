package app.boardmanager.dto;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Value;

public class BoardListReqDto implements Serializable{
	private static final long serialVersionUID = -5447464762567997494L;

	@Value("10")
	int pageLen;
	
	@Value("1")
	int page;
	
	@Value("0")
	int offset;
	
	@Value("SEQ")
	String orderCol;
	
	@Value("false")
	boolean isDesc;
	
	String searchCol;
	String searchKey;
	
	public BoardListReqDto() {}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public String getOrderCol() {
		return orderCol;
	}

	public void setOrderCol(String orderCol) {
		this.orderCol = orderCol;
	}

	public boolean isDesc() {
		return isDesc;
	}

	public void setDesc(boolean isDesc) {
		this.isDesc = isDesc;
	}

	public String getSearchCol() {
		return searchCol;
	}

	public void setSearchCol(String searchCol) {
		this.searchCol = searchCol;
	}

	public String getSearchKey() {
		return searchKey;
	}

	public void setSearchKey(String searchKey) {
		this.searchKey = searchKey;
	}

	public int getPageLen() {
		return pageLen;
	}

	public void setPageLen(int pageLen) {
		this.pageLen = pageLen;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	@Override
	public String toString() {
		return "BoardListReqDto [pageLen=" + pageLen + ", page=" + page + ", orderCol=" + orderCol + ", isDesc="
				+ isDesc + ", searchCol=" + searchCol + ", searchKey=" + searchKey + "]";
	}
	
	

}
