package analyzer.app.util;

import java.util.TreeSet;

public class CharSortedSet extends TreeSet<Character> {
	public CharSortedSet() {
		super((o1, o2) -> {
			// 0 이 리턴되면 중복 처리가 안됨
			if(compareToIgnoreCase(o1, o2) == 0) {
				if(Character.toUpperCase(o1)==o1){
                    return -1;
                }else{
                    return 1;
                }
			}else {
				return compareToIgnoreCase(o1, o2);
			}
		});
	}

	/**
	 * 원소 추가(영문자만)
	 */
	@Override
	public boolean add(Character e) {
		if(Character.isAlphabetic(e)) {
			return super.add(e);
		}else {
//			System.out.println("Cannot add charactor '"+e+"'");
			return false;
		}
	}
	
	/**
	 * 두 문자의 작고 큼을 비교(대소문자 무시)
	 * 
	 * @param o1
	 * @param o2
	 * @return
	 */
	private static int compareToIgnoreCase(char o1, char o2) {
		return Character.compare(Character.toUpperCase(o1), Character.toUpperCase(o2));
	}
}
