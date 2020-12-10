package analyzer.app.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import analyzer.app.common.exception.ExpectedException;
import analyzer.app.dto.AnalyzeReqDto;
import analyzer.app.dto.AnalyzeResDto;
import analyzer.app.util.CharSortedSet;
import analyzer.app.util.NumSortedSet;

@Service
public class AnalyzeService {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	public AnalyzeResDto doAnalyze(AnalyzeReqDto reqDto) throws ExpectedException, IOException {
		Set[] contentsSet			= callHttp(reqDto.getUrl(), reqDto.getType());
		char[] content 				= convertCharacterNumberCrossPrint(contentsSet);
		String[] splittedContent 	= contentSplitByLength(new String(content), reqDto.getUnit());
		
		return new AnalyzeResDto().setCut(splittedContent[0]).setRemainder(splittedContent[1]);
	}
	
	/**
	 * HTTP 호출(GET)
	 * 
	 * @param urlString
	 * @param type
	 * @return
	 * @throws IOException
	 */
	private Set[] callHttp(String urlString, int type) throws IOException {
		HttpURLConnection con = (HttpURLConnection) new URL(urlString).openConnection();
		con.setRequestMethod("GET");
		logger.info("Response Code : " + con.getResponseCode());
		
		return readHttpContents(con.getInputStream(), type);
	}
	
	/**
	 * HTTP Response Payload 부분 Read.
	 * 
	 * @param inputStream
	 * @param type
	 * @return
	 */
	private Set[] readHttpContents(InputStream inputStream, int type){
		CharSortedSet charSet = new CharSortedSet();
		NumSortedSet numSet   = new NumSortedSet();
		
		try (BufferedReader in = new BufferedReader(new InputStreamReader(inputStream))){
			int charactor;
			boolean tagStart = false;
			while ((charactor = in.read()) != -1) {
				if(AnalyzeReqDto.ANALYZE_TYPE.WITHOUT_HTML.getType() == type) {
					if(charactor == '<') {
						tagStart = true;
					}else if(charactor == '>') {
						tagStart = false;
					}
				}
				
				if(!tagStart) {
					addCharacterOrNumberSet(charactor, charSet, numSet);
				}
			}

		}catch(IOException e) {
			logger.error("Error! [{}]", e.getMessage());
		}catch(Exception e) {
			logger.error("Error! [{}]", e.getMessage());
		}
		
		return new Set[] {charSet, numSet};
	}
	
	/**
	 * char 객체를 CharacterSet, NumberSet에 분배하여 삽입
	 * 
	 * @param charactor
	 * @param charSet
	 * @param numSet
	 * @return
	 */
	private boolean addCharacterOrNumberSet(int charactor, CharSortedSet charSet, NumSortedSet numSet) {
		if(Character.isDigit(charactor)) {
			numSet.add((char)charactor);
        }else if(Character.isAlphabetic(charactor)) {
        	charSet.add((char)charactor);
        }else{
        	return false;
        }
		
		return true;
	}
	
	/**
	 * char 객체를 교차하여 삽입
	 * 
	 * @param contentsSet
	 * @return
	 */
	private char[] convertCharacterNumberCrossPrint(Set[] contentsSet) {
		CharSortedSet charSet = (CharSortedSet)contentsSet[0];
		NumSortedSet numSet   = (NumSortedSet)contentsSet[1];
		
		char[] newCrossCharArray = new char[charSet.size() + numSet.size()];
		
		Character c1 = null;
		Character c2 = null;
		
		int index = 0;
		boolean endOfChar = false;
		boolean endOfNum  = false;
		while(index < newCrossCharArray.length) {
			if(endOfNum || index%2 == 0) {
				c1 = charSet.pollFirst();
				if(c1 != null) {
					newCrossCharArray[index++] = c1;
				}else {
					endOfChar = true;
				}
			}else if(endOfChar || index%2 == 1) {
				c2 = numSet.pollFirst();
				if(c2 != null) {
					newCrossCharArray[index++] = c2;
				}else {
					endOfNum = true;
				}
			}
		}
		
		return newCrossCharArray;
	}
	
	/**
	 * 지정 단위로 content를 나누어(CarrageReturn 처리) 줌
	 * 
	 * @param content
	 * @param unit
	 * @return
	 */
	private String[] contentSplitByLength(String content, int unit) {
		
		int totalLen = content.length();
		int idx = 0;
		StringBuffer sb  = new StringBuffer();
		StringBuffer sb2 = new StringBuffer();
		while(idx < totalLen) {
			if(totalLen-idx >= unit) {
				sb.append(content.substring(idx, idx+unit)).append("\r\n");
				idx = idx+unit;
			}else {
				sb2.append(content.substring(idx, totalLen));
				break;
			}
		}
		
		return new String[] {sb.toString(), sb2.toString()};
	}
	
	
}
