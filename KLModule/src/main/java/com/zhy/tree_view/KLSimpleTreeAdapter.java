package com.zhy.tree_view;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.zhy.tree.bean.KLNode;
import com.zhy.tree.bean.KLTreeListViewAdapter;

import java.util.List;

public class KLSimpleTreeAdapter<T> extends KLTreeListViewAdapter<T> {

    TextView tx=null;
    String s ="";

    public KLSimpleTreeAdapter(TextView txv, ListView mTree, final Context context, List<T> datas,
                               int defaultExpandLevel) throws IllegalArgumentException,
            IllegalAccessException {
        super(mTree, context, datas, defaultExpandLevel);
        tx=txv;

        mTree.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                expandOrCollapse(position);
            }

        });

    }


    @Override
    public View getConvertView(KLNode node, int position, View convertView, ViewGroup parent) {
        final int index = position;
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.kllist_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.icon = (ImageView) convertView
                    .findViewById(R.id.id_treenode_icon);
            viewHolder.label = (TextView) convertView
                    .findViewById(R.id.id_treenode_label);
            viewHolder.cb = (CheckBox) convertView
                    .findViewById(R.id.item_cb);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (node.getIcon() == -1) {
            viewHolder.icon.setVisibility(View.INVISIBLE);
        } else {
            viewHolder.icon.setVisibility(View.VISIBLE);
            viewHolder.icon.setImageResource(node.getIcon());
        }


        viewHolder.cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    mAllNodes.get(index).isChecked = true;
                    mBean.get(index).isChecked = true;

                } else {
                    mAllNodes.get(index).isChecked = false;
                    mBean.get(index).isChecked = false;
                }
                s="";int i =0;
                for(KLNode n:mAllNodes)
                {
                    if(n.isChecked&&n.getId()>10000) {i++;s=s+n.getName()+" ";}
                    if(n.isChecked&&n.getId()<10000) {s=s+n.getName()+": ";}

                }
                s="（已选"+ String.valueOf(i) + "人）"+s;
                tx.setText(s);
              /*  List<KLNode> fuckbug = mAllNodes.get(index).getChildren();
                if (fuckbug != null)
                    for (KLNode myloly : fuckbug) {
                        myloly.isChecked = isChecked;
                    }*/
                notifyDataSetChanged();

            }
        });

        viewHolder.label.setText(node.getName());
        viewHolder.cb.setChecked(node.isChecked);



        return convertView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        KLNode node = mNodes.get(position);
        convertView = getConvertView(node, position, convertView, parent);
        convertView.setPadding(node.getLevel() * 30, 3, 3, 3);
        return convertView;
    }

    @Override
    public int getCount() {
        return mAllNodes.size();
    }

    public List<KLNode> getAllNode()
    {
        return mAllNodes;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    class ViewHolder {
        ImageView icon;
        TextView label;
        CheckBox cb;
    }



}