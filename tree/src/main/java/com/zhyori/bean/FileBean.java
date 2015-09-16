package com.zhyori.bean;

import com.zhyori.tree.bean.TreeNodeId;
import com.zhyori.tree.bean.TreeNodeLabel;
import com.zhyori.tree.bean.TreeNodePid;

public class FileBean
{
	@TreeNodeId
	private int _id;
	@TreeNodePid
	private int parentId;
	@TreeNodeLabel
	private String name;
	private long length;
	private String desc;
	//id和父节点的id以及名称
	public FileBean(int _id, int parentId, String name)
	{
		super();
		this._id = _id;
		this.parentId = parentId;
		this.name = name;
	}

}
