package com.bmj.tree.bean;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.bmj.tree_view.R;
/**
 * http://blog.csdn.net/lmj623565791/article/details/40212367
 * @author zhy
 *
 */
public class CommonContactTreeHelper
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
	public static <T> List<CommonContactNode> getSortedNodes(List<T> datas,
			int defaultExpandLevel) throws IllegalArgumentException,
			IllegalAccessException

	{
		List<CommonContactNode> result = new ArrayList<CommonContactNode>();
		// 将用户数据转化为List<CommonContactNode>
		List<CommonContactNode> nodes = convetData2Node(datas);
		// 拿到根节点
		List<CommonContactNode> rootNodes = getRootNodes(nodes);
		// 排序以及设置Node间关系
		for (CommonContactNode node : rootNodes)
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
	public static List<CommonContactNode> filterVisibleNode(List<CommonContactNode> nodes)
	{
		List<CommonContactNode> result = new ArrayList<CommonContactNode>();

		for (CommonContactNode node : nodes)
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
	private static <T> List<CommonContactNode> convetData2Node(List<T> datas)
			throws IllegalArgumentException, IllegalAccessException

	{
		List<CommonContactNode> nodes = new ArrayList<CommonContactNode>();
		CommonContactNode node = null;

		for (T t : datas)
		{
			int id = -1;
			int pId = -1;
			String label = null;
			Boolean isChecked =false;
			Class<? extends Object> clazz = t.getClass();
			Field[] declaredFields = clazz.getDeclaredFields();
			for (Field f : declaredFields)
			{
				if (f.getAnnotation(CommonContactTreeNodeId.class) != null)
				{
					f.setAccessible(true);
					id = f.getInt(t);
				}
				if (f.getAnnotation(CommonContactTreeNodePid.class) != null)
				{
					f.setAccessible(true);
					pId = f.getInt(t);
				}
				if (f.getAnnotation(CommonContactTreeNodeLabel.class) != null)
				{
					f.setAccessible(true);
					label = (String) f.get(t);
				}
				if (f.getAnnotation(CommonContactTreeNodeisChecked.class) != null)
				{
					f.setAccessible(true);
					isChecked = (Boolean) f.get(t);
				}
				if (id != -1 && pId != -1 && label != null)
				{
					break;
				}
			}
			node = new CommonContactNode(id, pId, label,t,isChecked);
			nodes.add(node);
		}

		/**
		 * 设置Node间，父子关系;让每两个节点都比较一次，即可设置其中的关系
		 */
		for (int i = 0; i < nodes.size(); i++)
		{
			CommonContactNode n = nodes.get(i);
			for (int j = i + 1; j < nodes.size(); j++)
			{
				CommonContactNode m = nodes.get(j);
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
		for (CommonContactNode n : nodes)
		{
			setNodeIcon(n);
		}
		return nodes;
	}

	private static List<CommonContactNode> getRootNodes(List<CommonContactNode> nodes)
	{
		List<CommonContactNode> root = new ArrayList<CommonContactNode>();
		for (CommonContactNode node : nodes)
		{
			if (node.isRoot())
				root.add(node);
		}
		return root;
	}

	/**
	 * 把一个节点上的所有的内容都挂上去
	 */
	private static void addNode(List<CommonContactNode> nodes, CommonContactNode node,
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
			addNode(nodes, (CommonContactNode) node.getChildren().get(i), defaultExpandLeval,
					currentLevel + 1);
		}
	}

	/**
	 * 设置节点的图标
	 * 
	 * @param node
	 */
	private static void setNodeIcon(CommonContactNode node)
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
			node.setIcon(R.drawable.comman_portrait_corner);
		else if(node.isLeaf())
		{
			node.setIcon(R.drawable.tree_ex);
		}
	}

}
