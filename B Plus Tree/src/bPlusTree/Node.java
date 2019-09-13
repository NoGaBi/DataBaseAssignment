package bPlusTree;

/**
* Copyright 2018 NoGaBi
*/

import java.util.ArrayList;

import bPlusTree.BPlusTree.Tree;

public abstract class Node {
	/**
	 * 
	 */
	//Node type define
	public static final int VALUE_NODE = -1;
	public static final int LEAF_NODE = 0;
	public static final int NON_LEAF_NODE = 1;
	//Search type define
	public static final int SMALLEST_SEARCH = 7;
	public static final int BIGGEST_SEARCH = 8;
	public static final int MIDDLE_SEARCH = 9;
	public static final int INSERT_SEARCH = 10;
	public static final int KEY_SEARCH = 11;

	
	protected int typeOfNode;
	/* 0�ϰ�� Leaf Node
	 * 1�ϰ�� Non-Leaf Node
	 * -1�ϰ�� Value Node (Integer�� Value ������ ���� Node)
	 */
	protected Tree tree;
	protected int key;
	protected int maxSize;
	protected ArrayList<Node> p;
	protected int m;//Number of keys
	protected Node r;
	protected boolean active;
	/* Non-Leaf Node: rightmost child node
	 * Leaf Node: left sibling node
	 * Value Node: right sibling Value node
	 */
	protected Node l;
	protected Node parent = null;
	
	@Override
	public boolean equals(Object arg0) {
		return this.key == ((Node)arg0).key;
	}
	
	public abstract void Insert(Node node);
	public abstract Node Search(int key);
	public abstract int IndexOf(int type);
	public abstract boolean Delete(Node node);
}
