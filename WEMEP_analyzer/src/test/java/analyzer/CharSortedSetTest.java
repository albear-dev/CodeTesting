package analyzer;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;

import java.lang.reflect.Method;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.powermock.reflect.Whitebox;

import analyzer.app.util.CharSortedSet;

public class CharSortedSetTest {
	@Test
	void 문자_숫자_Set_추가_캐릭터만() throws Exception {
		// Given
		CharSortedSet charSet = new CharSortedSet();
		charSet.add('a');
		charSet.add('1');
		charSet.add('A');
		charSet.add('!');
		charSet.add('[');
		charSet.add('9');
		charSet.add('K');
		charSet.add('<');
		charSet.add('z');
		charSet.add('A');
		
		// When
		String charSetString = charSet.stream().map(Object::toString).collect(Collectors.joining());

		// Then
		assertThat("AAaKz", equalTo(charSetString));
	}

	@Test
	void 대소문자_무시_같은지_비교() throws Exception {
		// Given
		Method method = Whitebox.getMethod(CharSortedSet.class, "compareToIgnoreCase", Character.TYPE, Character.TYPE);
		
		// When
		int whenSameUpperCaseAlphabet 		= (int) method.invoke(null, 'A', 'A');
		int whenSameDifferentCaseAlphabet 	= (int) method.invoke(null, 'A', 'a');
		int whenSameDifferentCaseAlphabet2 	= (int) method.invoke(null, 'a', 'A');
		int whenDifferentTypeAlphabet 		= (int) method.invoke(null, 'A', '1');
		int whenSameLowerCaseAlphabet 		= (int) method.invoke(null, 'a', 'a');
		int whenDifferentCaseAlphabet 		= (int) method.invoke(null, 'a', 'K');
		
		// Then
		assertThat(0, equalTo(whenSameUpperCaseAlphabet));
		assertThat(0, equalTo(whenSameDifferentCaseAlphabet));
		assertThat(0, equalTo(whenSameDifferentCaseAlphabet2));
		assertThat(0, lessThan(whenDifferentTypeAlphabet));
		assertThat(0, equalTo(whenSameLowerCaseAlphabet));
		assertThat(0, greaterThan(whenDifferentCaseAlphabet));

	}

}
