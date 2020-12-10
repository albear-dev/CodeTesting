package analyzer;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import analyzer.app.util.NumSortedSet;

public class NumSortedSetTest {
	@Test
	void 문자_숫자_Set_추가_숫자만() throws Exception{
		
		// Given
		NumSortedSet numSet = new NumSortedSet();
		numSet.add('a');
		numSet.add('1');
		numSet.add('!');
		numSet.add('[');
		numSet.add('9');
		numSet.add('7');
		numSet.add('7');
		numSet.add('7');
		numSet.add('K');
		numSet.add('<');
		numSet.add('z');
		numSet.add('A');
		
		// When
		String numSetString = numSet.stream().map(Object::toString).collect(Collectors.joining());
		
		// Then
		assertThat("17779", equalTo(numSetString));
	}
}
