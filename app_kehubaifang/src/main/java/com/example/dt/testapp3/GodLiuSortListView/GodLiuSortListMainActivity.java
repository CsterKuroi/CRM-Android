package com.example.dt.testapp3.GodLiuSortListView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.example.dt.testapp3.GodLiuSortListView.GodLiuSideBar.OnTouchingLetterChangedListener;
import com.example.dt.testapp3.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//import com.example.dt.testapp3.Form.FormActivity;
//import com.example.dt.testapp3.Form.Form_Adapter_Write;
//import com.example.dt.testapp3.Form.Form_Item;
//import com.example.dt.testapp3.Tools;

public class GodLiuSortListMainActivity extends Activity{
	private ListView sortListView;
	private GodLiuSideBar godLiuSideBar;
	private TextView dialog;
	private GodLiuSortAdapter adapter;
	private GodLiuClearEditText mGodLiuClearEditText;
    private boolean multichoice;
    ArrayList<Integer> indexList;

    //类型
    public static final int TYPE_NORMAL=0, TYPE_PLACE=1, TYPE_ITEM=2;
    private int TYPE;

	/**
	 * 汉字转换成拼音的类
	 */
	private GodLiuCharacterParser godLiuCharacterParser;
	private List<GodLiuSortModel> SourceDateList;
	
	/**
	 * 根据拼音来排列ListView里面的数据类
	 */
	private GodLiuPinyinComparator godLiuPinyinComparator;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sort_list_godliu_activity_main);			//todo
		initViews();
	}


	private void initViews() {

//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED);

        //得到父Activity的参数
        Intent intent = getIntent();
        setTitle(intent.getStringExtra("title"));
        String[] data = intent.getStringArrayExtra("data");
        multichoice = intent.getBooleanExtra("multichoice", false);
        indexList = intent.getIntegerArrayListExtra("indexList");
        TYPE = intent.getIntExtra("type", TYPE_NORMAL);


        //实例化汉字转拼音类
		godLiuCharacterParser = GodLiuCharacterParser.getInstance();
		
		godLiuPinyinComparator = new GodLiuPinyinComparator();
		
		godLiuSideBar = (GodLiuSideBar) findViewById(R.id.sidrbar);				//todo
		dialog = (TextView) findViewById(R.id.dialog);
		godLiuSideBar.setTextView(dialog);
		
		//设置右侧触摸监听
		godLiuSideBar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {
			
			@Override
			public void onTouchingLetterChanged(String s) {
				//该字母首次出现的位置
				int position = adapter.getPositionForSection(s.charAt(0));
				if(position != -1){
					sortListView.setSelection(position);
				}
				
			}
		});

        //------------------------------点击监听·返回------------------------------
		sortListView = (ListView) findViewById(R.id.country_lvcountry);				//todo
		sortListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				//这里要利用adapter.getItem(position)来获取当前position所对应的对象

                //单选
                if(!multichoice) {
                    Intent intent = new Intent();
                    String str = ((GodLiuSortModel) adapter.getItem(position)).getName();
                    if(TYPE == TYPE_ITEM){
                        String strs[] = str.split(", ");
                        str = strs[0] + " inventories "+strs[1];
                    }
                    intent.putExtra("data", str);
                    intent.putExtra("index", ((GodLiuSortModel)adapter.getItem(position)).index);
                    setResult(RESULT_OK, intent);
                    finish();
                }

                //多选
                else{
                    GodLiuSortModel godLiuSortModel = (GodLiuSortModel) adapter.getItem(position);
                    godLiuSortModel.setChecked(!godLiuSortModel.isChecked());
                    adapter.notifyDataSetChanged();
                }
			}
		});

		SourceDateList = filledData(data);
		
		// 根据a-z进行排序源数据
		Collections.sort(SourceDateList, godLiuPinyinComparator);

        //设置checkbox
        if(multichoice) {
            for (int i = 0; i < indexList.size(); i++) {
                SourceDateList.get(indexList.get(i)).setChecked(true);
            }
        }


        adapter = new GodLiuSortAdapter(this, SourceDateList, multichoice);
		sortListView.setAdapter(adapter);

		
		mGodLiuClearEditText = (GodLiuClearEditText) findViewById(R.id.filter_edit);		//todo
		
		//根据输入框输入值的改变来过滤搜索
		mGodLiuClearEditText.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				//当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
				filterData(s.toString());
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
			}
		});
	}


	/**
	 * ΪListView�������
	 * @param date
	 * @return
	 */
	private List<GodLiuSortModel> filledData(String [] date){
		List<GodLiuSortModel> mSortList = new ArrayList<GodLiuSortModel>();
		if (date == null){
			return mSortList;
		}
		for(int i=0; i<date.length; i++){
			GodLiuSortModel godLiuSortModel = new GodLiuSortModel();
			godLiuSortModel.setName(date[i]);
            godLiuSortModel.setChecked(false);
            godLiuSortModel.index=i;
			//汉字转换成拼音
			String pinyin = godLiuCharacterParser.getSelling(date[i]);
			String sortString = pinyin.substring(0, 1).toUpperCase();
			
			// 正则表达式，判断首字母是否是英文字母
			if(sortString.matches("[A-Z]")){
				godLiuSortModel.setSortLetters(sortString.toUpperCase());
			}else{
				godLiuSortModel.setSortLetters("#");
			}
			
			mSortList.add(godLiuSortModel);
		}

		return mSortList;
		
	}
	
	/**
	 * 根据输入框中的值来过滤数据并更新ListView
	 * @param filterStr
	 */
	private void filterData(String filterStr){
		List<GodLiuSortModel> filterDateList = new ArrayList<GodLiuSortModel>();
		
		if(TextUtils.isEmpty(filterStr)){
			filterDateList = SourceDateList;
		}else{
			filterDateList.clear();
			for(GodLiuSortModel godLiuSortModel : SourceDateList){
				String name = godLiuSortModel.getName();
				if(name.indexOf(filterStr.toString()) != -1 || godLiuCharacterParser.getSelling(name).startsWith(filterStr.toString())){
					filterDateList.add(godLiuSortModel);
				}
			}
		}
		
		// 根据a-z进行排序
		Collections.sort(filterDateList, godLiuPinyinComparator);
		adapter.updateListView(filterDateList);
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if(multichoice)
            getMenuInflater().inflate(R.menu.menu_sort_list_godliu, menu);				//todo

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent intent = new Intent();

        if (id == android.R.id.home) {
			setResult(RESULT_CANCELED);
			GodLiuSortListMainActivity.this.finish();
		} else if (id == R.id.sortlist_ok) {
			intent.putExtra("data", getMultiChoice());

			setResult(RESULT_OK, intent);
			finish();
		}
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if(keyCode == KeyEvent.KEYCODE_BACK){

            setResult(RESULT_CANCELED);
            GodLiuSortListMainActivity.this.finish();
        }

        return super.onKeyDown(keyCode, event);
    }

    //返回多个选项
    public ArrayList<Object> getMultiChoice(){
        ArrayList<String> strList = new ArrayList<String>();
        ArrayList<Integer> indexList = new ArrayList<Integer>();
        GodLiuSortModel model;

        for(int i=0; i<SourceDateList.size(); i++){
            model = SourceDateList.get(i);
            if(model.isChecked()) {
                strList.add(model.getName());
                indexList.add(i);
            }
        }

        ArrayList<Object> list = new ArrayList<Object>();
        list.add(strList);
        list.add(indexList);


        return list;
    }

}
