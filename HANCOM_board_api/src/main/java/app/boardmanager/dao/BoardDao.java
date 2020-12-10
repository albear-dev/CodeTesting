package app.boardmanager.dao;

import java.util.List;

import app.boardmanager.dto.BoardDeleteReqDto;
import app.boardmanager.dto.BoardDetailReqDto;
import app.boardmanager.dto.BoardDetailResDto;
import app.boardmanager.dto.BoardListReqDto;
import app.boardmanager.dto.BoardListResDto;
import app.boardmanager.dto.BoardModifyReqDto;
import app.boardmanager.dto.BoardRegReqDto;

public interface BoardDao{
	List<BoardListResDto> selectBoardList(BoardListReqDto boardListReqDto) throws Exception;
	BoardDetailResDto selectBoardDetail(BoardDetailReqDto boardDetailReqDto) throws Exception;
	int insertBoardContent(BoardRegReqDto boardRegReqDto) throws Exception;
	int modifyBoardContent(BoardModifyReqDto boardModifyReqDto) throws Exception;
	int deleteBoardContent(BoardDeleteReqDto boardDeleteReqDto) throws Exception;
}