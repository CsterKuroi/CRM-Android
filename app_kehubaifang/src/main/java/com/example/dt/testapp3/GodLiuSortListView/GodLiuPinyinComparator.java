package com.example.dt.testapp3.GodLiuSortListView;

import com.example.dt.testapp3.Graphics.GodLiuStructure;

import java.util.Comparator;

/**
 * 
 * @author xiaanming
 *
 */
public class GodLiuPinyinComparator implements Comparator<GodLiuSortModel>{

	public int compare(GodLiuSortModel o1, GodLiuSortModel o2) {
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

    public int compare(GodLiuStructure.User o1, GodLiuStructure.User o2){

        if (o1.sortLetters.equals("@")
                || o2.sortLetters.equals("#")) {
            return -1;
        } else if (o1.sortLetters.equals("#")
                || o2.sortLetters.equals("@")) {
            return 1;
        } else {
            return o1.sortLetters.compareTo(o2.sortLetters);
        }
    }

}
