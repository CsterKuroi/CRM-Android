package com.bmj.tree.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * http://blog.csdn.net/lmj623565791/article/details/40212367
 *
 * @author zhy
 */
public class CommonContactNode<T> {
    private T t;
    private int id;
    /**
     * 根节点pId为0
     */
    private int pId = 0;

    private String name;

    /**
     * 当前的级别
     */
    private int level;

    /**
     * 是否展开
     */
    private boolean isExpand = false;
    /*
    * 是否选中
    * */
    public boolean isChecked =false;

    private int icon;

    /**
     * 下一级的子Node
     */
    private List<CommonContactNode> children = new ArrayList<CommonContactNode>();

    /**
     * 父Node
     */
    private CommonContactNode parent;

    public CommonContactNode() {
    }


    public CommonContactNode(int id, int pId, String name, T t) {
        super();
        this.id = id;
        this.pId = pId;
        this.name = name;
        this.t = t;
    }

    public CommonContactNode(int id, int pId, String name, T t,Boolean isChecked) {
        super();
        this.id = id;
        this.pId = pId;
        this.name = name;
        this.t = t;
        this.isChecked = isChecked;
    }


    public CommonContactNode(int id, int pId, String name) {
        super();
        this.id = id;
        this.pId = pId;
        this.name = name;
    }

    public T getT() {
        return t;
    }

    public void setT(T t) {
        this.t = t;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getpId() {
        return pId;
    }

    public void setpId(int pId) {
        this.pId = pId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public boolean isExpand() {
        return isExpand;
    }

    public List<CommonContactNode> getChildren() {
        return children;
    }

    public void setChildren(List<CommonContactNode> children) {
        this.children = children;
    }

    public CommonContactNode getParent() {
        return parent;
    }

    public void setParent(CommonContactNode parent) {
        this.parent = parent;
    }

    /**
     * 是否为跟节点
     *
     * @return
     */
    public boolean isRoot() {
        return parent == null;
    }

    /**
     * 判断父节点是否展开
     *
     * @return
     */
    public boolean isParentExpand() {
        if (parent == null)
            return false;
        return parent.isExpand();
    }

    /**
     * 是否是叶子界点
     *
     * @return
     */
    public boolean isLeaf() {
        return children.size() == 0;
    }

    /**
     * 获取level
     */
    public int getLevel() {
        return parent == null ? 0 : parent.getLevel() + 1;
    }

    /**
     * 设置展开
     *
     * @param isExpand
     */
    public void setExpand(boolean isExpand) {
        this.isExpand = isExpand;
        if (!isExpand) {

            for (CommonContactNode node : children) {
                node.setExpand(isExpand);
            }
        }
    }

}
