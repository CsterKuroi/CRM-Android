package com.example.renxin.shujia.SortListView;

public class crmSortModel {

	private String name;   //��ʾ������
	private String sortLetters;  //��ʾ����ƴ��������ĸ
    private boolean checked;
	
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
