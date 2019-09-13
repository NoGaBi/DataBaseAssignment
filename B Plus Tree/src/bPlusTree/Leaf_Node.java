package bPlusTree;

/**
* Copyright 2018 NoGaBi
*/

import java.util.ArrayList;

import bPlusTree.BPlusTree.Tree;

public class Leaf_Node extends Node {

	/**
	 * 
	 */

	public Leaf_Node(int size, Tree tree) {
		this.typeOfNode = 0;
		this.maxSize = size;
		this.p = new ArrayList<Node>();
		this.tree = tree;
		this.active = true;
	}
	
	public void Insert(Node node) { //node is Value Node
		System.out.println("      insert "+node.key+" node in Leaf Node :"+this.key);
		Value_Node temp = (Value_Node)node;
		temp.parent = this;
		if(p.size() == 0) {
			p.add(temp);
		}
		else if(p.size() < this.maxSize) {
			int i;
			for(i = 0; i < p.size(); i++) {
				if( ((Value_Node)p.get(i)).getKey() > temp.getKey() ) break;
			}

			if(i != 0) {
				temp.r = p.get(i-1).r;
				temp.l = p.get(i-1);
				
				p.get(i-1).r = temp;
				if(temp.r != null) temp.r.l = temp;
			}
			else {
				temp.r = p.get(i);
				temp.l = p.get(i).l;
				
				p.get(i).l = temp;
				if(temp.l != null) temp.l.r = temp;
				
			}
			
			if(i == p.size()) {
				p.add(temp);
			}else p.add(i, temp);
		}else System.out.println("Error! size Overflow! check source code");
		
		if(p.size() == this.maxSize) {
			System.out.println("--lf 분할");
//			for(int k = 0; k < this.p.size(); k++) System.out.print(this.p.get(k).key);
//			System.out.println();
			
			if(this.parent == null) {
				Non_Leaf_Node nln = new Non_Leaf_Node(this.maxSize, this.tree);
				this.tree.setHeader(nln);
				this.parent = nln;
				nln.p.add(this);
			}
			
			Leaf_Node rightSibling = new Leaf_Node(this.maxSize, this.tree);
			for(int i = IndexOf(Node.MIDDLE_SEARCH); i < this.p.size();) {
				this.p.get(i).parent = rightSibling;
				rightSibling.p.add(this.p.remove(i));
			}
			this.key = this.p.get(0).key;
			rightSibling.key = rightSibling.p.get(0).key;
			System.out.println("     Lf-rightSibling key: "+ rightSibling.key);
			rightSibling.parent = this.parent;
			System.out.println("부모노드에 오른쪽 형제노드 삽입");
			this.parent.Insert(rightSibling);
			
		}
		
		this.key = this.p.get(IndexOf(Node.SMALLEST_SEARCH)).key; //key값 재설정
		
//		this.r = 
	}
	public Node Search(int key) {
		if(this.p.indexOf(new Value_Node(key,0)) >= 0) return this.p.get(this.p.indexOf(new Value_Node(key,0)));
		else {
			int i;
			for(i = 0;i < this.p.size();i++) {
				if(this.p.get(i).key > key) break;
			}
			if(i != this.p.size())return this.p.get(i);
			else return this.p.get(i-1).r;
		}
	}

	@Override
	public int IndexOf(int type) {
		if(type == Node.BIGGEST_SEARCH) return p.size() - 1;
		else if(type == Node.SMALLEST_SEARCH) return 0;
		else if(type == Node.MIDDLE_SEARCH) return this.maxSize/2;
		else return -2;
	}
	
	@Override
	public boolean Delete(Node node) {
		for(int i = 0; i < p.size(); i++) {
			System.out.println(p.get(i).key);
		}
		if(this.p.indexOf(node) >= 0) {
			//System.out.println("Delete in Leaf Node!");
			Node temp = p.get(p.indexOf(node));
			if(temp.l != null) temp.l.r = temp.r;
			if(temp.r != null) temp.r.l = temp.l;
			this.p.remove(this.p.indexOf(node));
			return true;
		}
		else {
			System.out.println("["+this.key+"[There is no Node having key: "+node.key);
		}
		return false;
	}
}
