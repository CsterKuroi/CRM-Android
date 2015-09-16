package com.example.spinel.myapplication.SortListView;

import com.example.spinel.myapplication.bpmStructure;

import java.util.Comparator;

/**
 * 
 * @author xiaanming
 *
 */
public class bpmPinyinComparator implements Comparator<bpmSortModel>{

	public int compare(bpmSortModel o1, bpmSortModel o2) {
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

    public int compare(bpmStructure.User o1, bpmStructure.User o2){

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
