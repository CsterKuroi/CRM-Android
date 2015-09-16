package com.bmj.tree_view;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bmj.tree.bean.CommonContactNode;
import com.bmj.tree.bean.CommonContactTreeListViewAdapter;

import java.util.List;

public class CommonContactSimpleTreeAdapter<T> extends CommonContactTreeListViewAdapter<T> {

    TextView tx=null;
    String s = "";
    public CommonContactSimpleTreeAdapter(TextView txv,ListView mTree, final Context context, List<T> datas,
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
    public View getConvertView(CommonContactNode node, int position, View convertView, ViewGroup parent) {
        final int index = position;

        //final CommonContactNode node2 = node;
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.commoncontact_list_item, parent, false);
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

                    s="";int i =0;
                    for(CommonContactNode n:mAllNodes)
                    {
                        if(n.isChecked&&n.getId()>10000) {i++;s=s+n.getName()+" ";}

                    }
                    s="（已选"+ String.valueOf(i) + "人）"+s;
                    tx.setText(s);

                } else {
                    mAllNodes.get(index).isChecked = false;
                    mBean.get(index).isChecked = false;
                    s="";int i = 0;
                    for(CommonContactNode n:mAllNodes)
                    {
                        if(n.isChecked&&n.getId()>10000)  {i++;s=s+n.getName()+" ";}

                    }
                    s="（已选"+ String.valueOf(i) + "人）"+s;
                    tx.setText(s);

                }
                List<CommonContactNode> fuckbug = mAllNodes.get(index).getChildren();
                if (fuckbug != null)
                    for (CommonContactNode myloly : fuckbug) {
                        myloly.isChecked = isChecked;
                    }
                notifyDataSetChanged();

            }
        });

        viewHolder.label.setText(node.getName());
        viewHolder.cb.setChecked(node.isChecked);



        return convertView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
try{
        CommonContactNode node = mNodes.get(position);
        convertView = getConvertView(node, position, convertView, parent);
        convertView.setPadding(node.getLevel() * 30, 3, 3, 3);}catch (IndexOutOfBoundsException e){}
        return convertView;
    }

    @Override
    public int getCount() {
        return mAllNodes.size();
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
