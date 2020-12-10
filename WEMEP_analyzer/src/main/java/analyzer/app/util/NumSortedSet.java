package analyzer.app.util;

import java.util.TreeSet;

public class NumSortedSet extends TreeSet<Character> {
	public NumSortedSet() {
		super((o1, o2) -> {
			// 0 이 리턴되면 중복 처리가 안됨
			int compare = Character.compare(o1, o2);
			return compare==0?-1:compare;
		});
	}
	
	@Override
	public boolean add(Character e) {
		if(Character.isDigit(e)) {
			return super.add(e);
		}else {
//			System.out.println("Cannot add charactor '"+e+"'");
			return false;
		}
	}

}
