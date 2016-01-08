package com.example.spinel.myapplication;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Spinel on 2015/7/23.
 */
public class bpmMyTabListener implements ActionBar.TabListener {
    public Fragment[] fragment;
    private String[] tag;
    int index = 0;
    boolean isSelected = false;

    public bpmMyTabListener(Fragment[] fragment, String[] tag){
        this.fragment = fragment;
        this.tag = tag;
    }


    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft){
//        Log.e("Fragment Transaction", tag[index]+" reselected");
//        if(!fragment[index].isAdded()) {
//            ft.add(fragment[index], tag[index]);
//            ft.replace(R.id.content, fragment[index], null);
//        }
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft){
//        Log.e("Fragment Transaction", tag[index]+" selected");
        if(!fragment[index].isAdded()) {
            ft.add(fragment[index], tag[index]);
            ft.replace(R.id.content, fragment[index], null);
        }

        isSelected = true;
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft){

//        Log.e("Fragment Transaction", tag[index]+" unselected");
        System.out.println(tag[index]+" unselected");
        ft.remove(fragment[index]);

        isSelected = false;
    }

    public void changeIndex(int index, FragmentTransaction ft){
    //    System.out.println("change index: "+Tools.Int2String(index));

        if(this.index!=index){
            if(isSelected) {
                ft.remove(fragment[this.index]);
                ft.add(fragment[index], tag[index]);
                ft.replace(R.id.content, fragment[index], null);
                ft.commit();
            }
            this.index = index;
        }

    }
}
