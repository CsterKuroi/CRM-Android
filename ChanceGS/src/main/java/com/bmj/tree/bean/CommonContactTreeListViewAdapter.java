package com.bmj.tree.bean;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.bmj.bean.CommonContactBean;

/**
 * http://blog.csdn.net/lmj623565791/article/details/40212367
 * @author zhy
 *
 * @param <T>
 */
public abstract class CommonContactTreeListViewAdapter<T> extends BaseAdapter
{

	protected Context mContext;
	/**
	 * 存储所有可见的Node
	 */
	protected List<CommonContactNode> mNodes;
	protected LayoutInflater mInflater;
	/**
	 * 存储所有的Node
	 */
	protected List<CommonContactNode> mAllNodes;
	protected List<CommonContactBean> mBean;
//	ViewHolder holder = null;

	/**
	 * 点击的回调接口
	 */
	private OnTreeNodeClickListener onTreeNodeClickListener;
	public List<CommonContactNode> getAllNode()
	{
		return mAllNodes;
	}
	public interface OnTreeNodeClickListener
	{
		void onClick(CommonContactNode node, int position);
	}

	public void setOnTreeNodeClickListener(
			OnTreeNodeClickListener onTreeNodeClickListener)
	{
		this.onTreeNodeClickListener = onTreeNodeClickListener;
	}

	/**
	 * 
	 * @param mTree
	 * @param context
	 * @param datas
	 * @param defaultExpandLevel
	 *            默认展开几级树
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public CommonContactTreeListViewAdapter(ListView mTree, Context context, List<T> datas,
											int defaultExpandLevel) throws IllegalArgumentException,
			IllegalAccessException
	{
		mContext = context;
		/**
		 * 对所有的Node进行排序,保持备份数据的一致性
		 */
		mBean = (List<CommonContactBean>) datas;
		mAllNodes = CommonContactTreeHelper.getSortedNodes(datas, defaultExpandLevel);
		int i = 0;
		CommonContactBean commoncontactbean;
		for(CommonContactNode ccnode:mAllNodes)
		{
			commoncontactbean = mBean.get(i);
			commoncontactbean.setpId(ccnode.getpId());
			commoncontactbean.setId(ccnode.getId());
			commoncontactbean.setLabel(ccnode.getName());
			commoncontactbean.setIsChecked(ccnode.isChecked);
			i++;
		}
		/**
		 * 过滤出可见的Node
		 */
		mNodes = CommonContactTreeHelper.filterVisibleNode(mAllNodes);
		mInflater = LayoutInflater.from(context);

		/**
		 * 设置节点点击时，可以展开以及关闭；并且将ItemClick事件继续往外公布
		 */
		mTree.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id)
			{
//				ViewHolder holder = (ViewHolder)view.getTag();
		//	holder.cb.setChecked(true);

				expandOrCollapse(position);

				if (onTreeNodeClickListener != null)
				{
					onTreeNodeClickListener.onClick(mNodes.get(position),
							position);
				}
			}

		});

	}

	/**
	 * 相应ListView的点击事件 展开或关闭某节点
	 * 
	 * @param position
	 */
	public void expandOrCollapse(int position)
	{
		CommonContactNode n = mNodes.get(position);

		if (n != null)// 排除传入参数错误异常
		{
			if (!n.isLeaf())
			{
				n.setExpand(!n.isExpand());
				mNodes = CommonContactTreeHelper.filterVisibleNode(mAllNodes);
				notifyDataSetChanged();// 刷新视图
			}
		}
	}

	@Override
	public int getCount()
	{
		return mNodes.size();
	}

	@Override
	public Object getItem(int position)
	{
		return mNodes.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{

		CommonContactNode node = mNodes.get(position);
		convertView = getConvertView(node, position, convertView, parent);
		convertView.setPadding(node.getLevel() * 30, 3, 3, 3);
		return convertView;
	}

	public abstract View getConvertView(CommonContactNode node, int position,
			View convertView, ViewGroup parent);

}
