package com.melnykov.fab.sample.SortListView;

import java.util.Comparator;

/**
 * 
 * @author xiaanming
 *
 */
public class crmPinyinComparator implements Comparator<crmSortModel>{

	public int compare(crmSortModel o1, crmSortModel o2) {
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
