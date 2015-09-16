package com.example.dt.testapp3.GodLiuSortListView;

public class GodLiuSortModel {

	private String name;   //显示的数据
	private String sortLetters;  //显示数据拼音的首字母
    private boolean checked;
    public int index;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSortLetters() {
		return sortLetters;
	}
	public void setSortLetters(String sortLetters) {
		this.sortLetters = sortLetters;
	}
    public boolean isChecked(){ return checked; }
    public void setChecked(boolean checked) {this.checked = checked;}
}
