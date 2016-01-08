package com.example.renxin.shujia.SortListView;

import java.util.Comparator;

/**
 * 
 * @author xiaanming
 *
 */
public class StudyPinyinComparator implements Comparator<StudySortModel>{

	public int compare(StudySortModel o1, StudySortModel o2) {
		if (o1.getSortLetters().equals("@")
				|| o2.getSortLetters().equals("#")) {
			return -1;
		} else if (o1.getSortLetters().equals("#")
				|| o2.getSortLetters().equals("@")) {
			return 1;
		} else {
			return o1.getSortLetters().compareTo(o2.getSortLetters());
		}
	}

}
