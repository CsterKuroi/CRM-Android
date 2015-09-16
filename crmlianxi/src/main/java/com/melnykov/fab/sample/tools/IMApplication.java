package com.melnykov.fab.sample.tools;


import android.content.Context;

import com.ricky.database.CenterDatabase;

public class IMApplication {

	public static String getUserid(Context c) {
		CenterDatabase cdb = new CenterDatabase(c, null);
		String userid = cdb.getUID();
		cdb.close();
		return userid;
	}
}
