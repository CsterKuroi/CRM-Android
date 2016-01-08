package com.zhy.tree.bean;

import com.zhy.tree_view.R;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
/**
 * http://blog.csdn.net/lmj623565791/article/details/40212367
 * @author zhy
 *
 */
public class KLTreeHelper
{
	/**
	 * 传入我们的普通bean，转化为我们排序后的Node
	 * 
	 * @param datas
	 * @param defaultExpandLevel
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public static <T> List<KLNode> getSortedNodes(List<T> datas,
			int defaultExpandLevel) throws IllegalArgumentException,
			IllegalAccessException

	{
		List<KLNode> result = new ArrayList<KLNode>();
		// 将用户数据转化为List<KLNode>
		List<KLNode> nodes = convetData2Node(datas);
		// 拿到根节点
		List<KLNode> rootNodes = getRootNodes(nodes);
		// 排序以及设置Node间关系
		for (KLNode node : rootNodes)
		{
			addNode(result, node, defaultExpandLevel, 1);
		}
		return result;
	}

	/**
	 * 过滤出所有可见的Node
	 * 
	 * @param nodes
	 * @return
	 */
	public static List<KLNode> filterVisibleNode(List<KLNode> nodes)
	{
		List<KLNode> result = new ArrayList<KLNode>();

		for (KLNode node : nodes)
		{
			// 如果为跟节点，或者上层目录为展开状态
			if (node.isRoot() || node.isParentExpand())
			{
				setNodeIcon(node);
				result.add(node);
			}
		}
		return result;
	}

	/**
	 * 将我们的数据转化为树的节点
	 * 
	 * @param datas
	 * @return
	 * @throws NoSuchFieldException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 */
	private static <T> List<KLNode> convetData2Node(List<T> datas)
			throws IllegalArgumentException, IllegalAccessException

	{
		List<KLNode> nodes = new ArrayList<KLNode>();
		KLNode node = null;

		for (T t : datas)
		{
			int id = -1;
			int pId = -1;
			String label = null;
			Boolean isChecked = false;
			Class<? extends Object> clazz = t.getClass();
			Field[] declaredFields = clazz.getDeclaredFields();
			for (Field f : declaredFields)
			{
				if (f.getAnnotation(KLTreeNodeId.class) != null)
				{
					f.setAccessible(true);
					id = f.getInt(t);
				}
				if (f.getAnnotation(KLTreeNodePid.class) != null)
				{
					f.setAccessible(true);
					pId = f.getInt(t);
				}
				if (f.getAnnotation(KLTreeNodeLabel.class) != null)
				{
					f.setAccessible(true);
					label = (String) f.get(t);
				}
				if (f.getAnnotation(KLTreeNodeisChecked.class) != null)
				{
					f.setAccessible(true);
					isChecked = (Boolean) f.get(t);
				}
				if (id != -1 && pId != -1 && label != null)
				{
					break;
				}
			}
			node = new KLNode(id, pId, label,t,isChecked);
			nodes.add(node);
		}

		/**
		 * 设置Node间，父子关系;让每两个节点都比较一次，即可设置其中的关系
		 */
		for (int i = 0; i < nodes.size(); i++)
		{
			KLNode n = nodes.get(i);
			for (int j = i + 1; j < nodes.size(); j++)
			{
				KLNode m = nodes.get(j);
				if (m.getpId() == n.getId())
				{
					n.getChildren().add(m);
					m.setParent(n);
				} else if (m.getId() == n.getpId())
				{
					m.getChildren().add(n);
					n.setParent(m);
				}
			}
		}

		// 设置图片
		for (KLNode n : nodes)
		{
			setNodeIcon(n);
		}
		return nodes;
	}

	private static List<KLNode> getRootNodes(List<KLNode> nodes)
	{
		List<KLNode> root = new ArrayList<KLNode>();
		for (KLNode node : nodes)
		{
			if (node.isRoot())
				root.add(node);
		}
		return root;
	}

	/**
	 * 把一个节点上的所有的内容都挂上去
	 */
	private static void addNode(List<KLNode> nodes, KLNode node,
			int defaultExpandLeval, int currentLevel)
	{

		nodes.add(node);
		if (defaultExpandLeval >= currentLevel)
		{
			node.setExpand(true);
		}

		if (node.isLeaf())
			return;
		for (int i = 0; i < node.getChildren().size(); i++)
		{
			addNode(nodes, (KLNode) node.getChildren().get(i), defaultExpandLeval,
					currentLevel + 1);
		}
	}

	/**
	 * 设置节点的图标
	 * 
	 * @param node
	 */
	private static void setNodeIcon(KLNode node)
	{
		if (node.getChildren().size() > 0 && node.isExpand())
		{
			node.setIcon(R.drawable.tree_ex);
		} else if (node.getChildren().size() > 0 && !node.isExpand())
		{
			node.setIcon(R.drawable.tree_ec);
		} else
		{	//如果是成员的话，设置一个头像。这里的是写死的。uglycode ！
			//node.setIcon(-1);
			node.setIcon(-1);
		}
		if(node.isLeaf()&&node.getId()>9999)
			node.setIcon(R.drawable.tt_default_user_portrait_corner_small);
		else if(node.isLeaf())
		{
			node.setIcon(R.drawable.tree_ex);
		}
	}

}
