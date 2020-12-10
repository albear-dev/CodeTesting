package analyzer;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.File;
import java.io.FileInputStream;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.powermock.reflect.Whitebox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import analyzer.app.service.AnalyzeService;
import analyzer.app.util.CharSortedSet;
import analyzer.app.util.NumSortedSet;

class AnalyzeServiceTest {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public AnalyzeServiceTest() {}
	private AnalyzeService service;
	
	@BeforeEach
	public void setup() throws Exception {
		service = new AnalyzeService();
	}
	
	@Test
	void Http통신_결과데이터_수신_및_처리() throws Exception{
		
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource("TestHtml.html").getFile());
		
		Set[] content = Whitebox.<Set[]>invokeMethod(service, "readHttpContents", new FileInputStream(file), 1);
		
		String contentCharacter = (String) content[0].stream().map(Object::toString).collect(Collectors.joining());
		String contentNumber    = (String) content[1].stream().map(Object::toString).collect(Collectors.joining());
		
		assertThat("aaaabbbCCcDddddEEeeeeeeeeehhhhhhhiiiiKllllllMmmmmmNnOoooPPRrrssTTttttttttttUuYyyy", equalTo(contentCharacter));
		assertThat("0011112349999", equalTo(contentNumber));
	}
	
	@Test
	void 문자열_숫자_Set_분배입력_테스트() throws Exception{
		CharSortedSet charSet = new CharSortedSet();
		NumSortedSet numSet   = new NumSortedSet();
		
		Boolean resultStatusChar1 			= Whitebox.<Boolean>invokeMethod(service, "addCharacterOrNumberSet", (int)'a', charSet, numSet);
		Boolean resultStatusChar2 			= Whitebox.<Boolean>invokeMethod(service, "addCharacterOrNumberSet", (int)'A', charSet, numSet);
		Boolean resultStatusNumber1 		= Whitebox.<Boolean>invokeMethod(service, "addCharacterOrNumberSet", (int)'1', charSet, numSet);
		Boolean resultStatusNumber2 		= Whitebox.<Boolean>invokeMethod(service, "addCharacterOrNumberSet", (int)'0', charSet, numSet);
		Boolean resultStatusSpecialChar1 	= Whitebox.<Boolean>invokeMethod(service, "addCharacterOrNumberSet", (int)'*', charSet, numSet);
		
		String contentCharacter = (String)charSet.stream().map(Object::toString).collect(Collectors.joining());
		String contentNumber    = (String)numSet.stream().map(Object::toString).collect(Collectors.joining());
		
		assertThat(true, equalTo(resultStatusChar1));
		assertThat(true, equalTo(resultStatusChar2));
		assertThat(true, equalTo(resultStatusNumber1));
		assertThat(true, equalTo(resultStatusNumber2));
		assertThat(false, equalTo(resultStatusSpecialChar1));
		
		assertThat("Aa", equalTo(contentCharacter));
		assertThat("01", equalTo(contentNumber));
		
		
	}
	
	@Test
	void 문자열_교체출력_정상_테스트() throws Exception{
		CharSortedSet charSet = new CharSortedSet();
		NumSortedSet numSet   = new NumSortedSet();
		
		charSet.add('a');
		charSet.add('B');
		charSet.add('c');
		charSet.add('C');
		charSet.add('d');
		charSet.add('d');
		charSet.add('d');
		charSet.add('e');
		numSet.add('3');
		numSet.add('2');
		numSet.add('1');
		numSet.add('1');
		
		Set[] contentsSet = new Set[] {charSet, numSet};
		
		char[] printedContent = Whitebox.<char[]>invokeMethod(service, "convertCharacterNumberCrossPrint", (Object)contentsSet);
		
		assertThat("a1B1C2c3ddde", equalTo(new String(printedContent)));
	}
	
	
	@Test
	void 문자열_분해_정상_테스트() throws Exception {
		int splitUnit = 10;
		String content = "abcdefghijklmnopqrstuvwxyz0987654321abcdefghijklmnopqrstuvwxyz0987654321abcdefghijklmnopqrstuvwxyz0987654321";
		
		String[] splitedContent = Whitebox.<String[]>invokeMethod(service, "contentSplitByLength", content, splitUnit);
		
		assertThat(100+(splitUnit*2), equalTo(splitedContent[0].length()));
		assertThat(8, equalTo(splitedContent[1].length()));
		assertThat("87654321", equalTo(new String(splitedContent[1])));
	}
}
