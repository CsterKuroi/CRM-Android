package com.zhyori.tree_view;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import com.zhyori.bean.Bean;
import com.zhyori.tree.bean.TreeListViewAdapter;

import java.util.ArrayList;
import java.util.List;

import de.tavendo.autobahn.WebSocketConnection;

public class MainActivity extends Activity {
	private List<Bean> mDatas = new ArrayList<Bean>();
	//private List<FileBean> mDatas2 = new ArrayList<FileBean>();
	private ListView mTree;
	private TreeListViewAdapter mAdapter;
	public static WebSocketConnection mConnection;
	private boolean isConnected = false;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

	}



	}