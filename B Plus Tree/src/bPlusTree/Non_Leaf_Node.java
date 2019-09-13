package bPlusTree;

/**
* Copyright 2018 NoGaBi
*/

import java.util.ArrayList;

import bPlusTree.BPlusTree.Tree;

public class Non_Leaf_Node extends Node {

	/**
	 * 
	 */

	public Non_Leaf_Node(int size, Tree tree) {
		this.typeOfNode = 1;
		this.maxSize = size;
		this.p = new ArrayList<Node>();
		this.tree = tree;
	}

	public void Insert(Node node) { // node is Leaf Node or Non-Leaf Node

		System.out.println("      insert " + node.key + " node in Non-Leaf Node :"
				+ this.p.get(IndexOf(Node.SMALLEST_SEARCH)).key);
		if (this.p.size() < 2 * this.maxSize + 1) {
			int i;
			for (i = 1; i < this.p.size(); i += 2) {
				if (this.p.get(i).key > node.key)
					break;
			}
			if (i < this.p.size())
				this.p.add(i, node);
			else
				this.p.add(node);
			Node temp2 = node;
			while (temp2.typeOfNode == Node.NON_LEAF_NODE) {
				temp2 = temp2.p.get(temp2.IndexOf(Node.SMALLEST_SEARCH));
			}
			if (i < this.p.size())
				this.p.add(i, temp2.p.get(temp2.IndexOf(Node.SMALLEST_SEARCH)));
			else
				this.p.add(this.p.size() - 1, temp2.p.get(temp2.IndexOf(Node.SMALLEST_SEARCH)));
			node.parent = this;

		} else
			System.out.println("Error: Array List of Non-Leaf Node is full!! check source code.");

		if (this.p.size() == 2 * this.maxSize + 1) {
			// for(int k = 0; k < this.p.size(); k++) System.out.print(this.p.get(k).key+"
			// ");
			// System.out.println();

			System.out.println("--nlf 분할");

			if (this.parent == null) {
				this.parent = new Non_Leaf_Node(this.maxSize, this.tree);
				this.tree.setHeader(this.parent);
				this.parent.p.add(this);
				this.parent.key = this.key;
			}

			Non_Leaf_Node rightSibling = new Non_Leaf_Node(this.maxSize, this.tree);
			ArrayList<Node> tempArr = new ArrayList<Node>();
			for (int i = 0; i < this.p.size(); i++) {
				if (this.p.get(i).typeOfNode != Node.VALUE_NODE)
					tempArr.add(this.p.get(i));
			} // tempArr에 포인터들만 다 넣어줌

			this.p.clear();
			p.add(tempArr.get(0));
			int i;
			for (i = 1; i < tempArr.size() / 2; i++) {
				Node temp = tempArr.get(i);
				while (temp.typeOfNode == Node.NON_LEAF_NODE)
					temp = temp.p.get(temp.IndexOf(Node.SMALLEST_SEARCH));
				this.p.add(temp.p.get(0));
				this.p.add(tempArr.get(i));
			}
			System.out.println(i);
			rightSibling.p.add(tempArr.get(i++));
			for (; i < tempArr.size(); i++) {
				Node temp = tempArr.get(i);
				while (temp.typeOfNode == Node.NON_LEAF_NODE)
					temp = temp.p.get(temp.IndexOf(Node.SMALLEST_SEARCH));
				rightSibling.p.add(temp.p.get(temp.IndexOf(Node.SMALLEST_SEARCH)));
				tempArr.get(i).parent = rightSibling;
				rightSibling.p.add(tempArr.get(i));
			}
			System.out.println("     N-LF-rightSibling key: " + rightSibling.key);

			rightSibling.parent = this.parent;
			for (int k = 0; k < rightSibling.p.size(); k += 2) {
				rightSibling.p.get(k).parent = rightSibling;
			}
			this.parent.Insert(rightSibling);

			rightSibling.key = rightSibling.p.get(rightSibling.IndexOf(Node.SMALLEST_SEARCH)).key;
		}

		Node rTemp = this.p.get(this.p.size() - 1);
		while (rTemp.typeOfNode == Node.NON_LEAF_NODE)
			rTemp = rTemp.p.get(rTemp.p.size() - 1);
		this.r = rTemp;
		this.key = this.p.get(IndexOf(Node.SMALLEST_SEARCH)).key;
	}

	public Node Search(int key) {
		for (int i = 1; i < p.size(); i += 2) {
			if (key < p.get(i).key) {
				return p.get(i - 1).Search(key);
			}
		}
		return p.get(p.size() - 1).Search(key);
	}

	@Override
	public int IndexOf(int type) {
		if (type == Node.BIGGEST_SEARCH)
			return p.size() - 1;
		else if (type == Node.SMALLEST_SEARCH)
			return 0;
		else if (type == Node.MIDDLE_SEARCH)
			return 2 * ((this.maxSize - 1) / 2) + 1;
		else
			return -2;
	}

	@Override
	public boolean Delete(Node node) {
		int i;
		for (i = 1; i < p.size(); i += 2) {
			if (node.key < p.get(i).key) {
				break;
			}
		}
		// System.out.println("i is: "+i);
		if (p.get(i - 1).Delete(node)) {
			if (p.get(i - 1).typeOfNode == Node.LEAF_NODE) {
				// CASE: Child is Leaf Node
				if (p.get(i - 1).p.isEmpty()) {
					// Delete in p
					if (i > 1 && p.get(i - 3).p.size() > 1) {
						p.get(i - 1).p.add(0, p.get(i - 3).p.remove(p.get(i - 3).p.size() - 1));
						p.get(i - 1).p.get(0).parent = p.get(i - 1);
						p.get(i - 1).key = p.get(i - 1).p.get(0).key;
						p.remove(i - 2);
						p.add(i - 2, p.get(i - 2).p.get(0));
					} else if (i + 1 < p.size() && p.get(i + 1).p.size() > 1) {
						p.get(i - 1).p.add(p.get(i + 1).p.remove(0));
						p.get(i - 1).p.get(p.get(i - 1).p.size() - 1).parent = p.get(i - 1);
						p.get(i - 1).key = p.get(i - 1).p.get(0).key;
						p.remove(i);
						p.add(i, p.get(i).p.get(0));
					} else {
						if (i == 1) {
							p.remove(i - 1);
							p.remove(i - 1);
							key = p.get(0).key;
						} else {
							p.remove(i - 1);
							p.remove(i - 2);
						}
					}
				}
			} 
//			else if (p.get(i - 1).typeOfNode == Node.NON_LEAF_NODE) {
//				// CASE: Child is Non-Leaf Node
//				if (p.get(i - 1).p.size() == 1) {
//					if (i > 1 && p.get(i - 3).p.size() > 3) {
//						p.get(i - 1).p.add(0, new Value_Node(p.get(i - 1).p.get(0).key, 0)); // 뒤 노드에 뒤 노드 맨 앞 노드이ㅡ 키 값
//																								// 삽입
//						p.get(i - 1).p.add(0, p.get(i - 3).p.remove(p.get(i - 3).p.size() - 1));// 뒤 노드에 앞 노드의 최대 우변 노드
//																								// 삽입
//						p.get(i - 3).p.remove(p.get(i - 3).p.size() - 1);// 앞 노드의 최대키값 삭제
//						p.get(i - 1).p.get(0).parent = p.get(i - 1);// 뒤 노드의 첫 노드 부모 설정
//						p.get(i - 1).key = p.get(i - 1).p.get(0).key;// "키값 설정
//						p.remove(i - 2);// 사이키값 삭제
//						p.add(i - 2, new Value_Node(p.get(i - 2).key, 0));// 사이키값 추가
//					} else if (i + 2 < p.size() && p.get(i + 1).p.size() > 3) {
//						p.get(i - 1).p.add(new Value_Node(p.get(i + 1).p.get(0).key, 0));
//						p.get(i - 1).p.add(p.get(i + 1).p.remove(0));
//						p.get(i + 1).p.remove(0);
//						p.get(i + 1).key = p.get(i + 1).p.get(0).key;
//						p.get(i - 1).p.get(p.get(i - 1).p.size() - 1).parent = p.get(i - 1);
//						p.get(i - 1).key = p.get(i - 1).p.get(0).key;
//						p.remove(i);
//						p.add(i, new Value_Node(p.get(i).key, 0));
//					} else {
//						if (i == 1) {
//							p.get(i + 1).p.add(0, p.get(i - 1).p.remove(0));
//							p.get(i + 1).p.get(0).parent = p.get(i + 1);
//							p.get(i + 1).p.add(1, new Value_Node(p.get(i + 1).p.get(1).key, 0));
//							p.get(i + 1).key = p.get(i + 1).p.get(0).key;
//							p.remove(i - 1);
//							p.remove(i - 1);
//							key = p.get(0).key;
//						} else {
//							p.get(i - 3).p.add(new Value_Node(p.get(i - 1).p.get(0).key, 0));
//							p.get(i - 3).p.add(p.get(i - 1).p.remove(0));
//							p.get(i - 3).p.get(p.get(i - 3).p.size() - 1).parent = p.get(i - 3);
//							p.remove(i - 1);
//							p.remove(i - 2);
//						}
//					}

					// if(i == 1) {
					// if(p.get(i+1).p.size() <= 2 * maxSize - 1) {
					// Node keyTemp = p.get(i+1).p.get(0);
					// while(keyTemp.typeOfNode == Node.NON_LEAF_NODE) keyTemp = keyTemp.p.get(0);
					// keyTemp = keyTemp.p.get(0);
					// p.get(i+1).p.add(0, keyTemp);
					// p.get(i+1).p.add(0, p.get(i-1).p.remove(0));
					// p.get(i+1).key = p.get(i+1).p.get(0).key;
					// p.remove(i-1);
					// p.remove(i-1);
					// }
					// else {
					// Node keyTemp = p.get(i+1).p.get(0);
					// while(keyTemp.typeOfNode == Node.NON_LEAF_NODE) keyTemp = keyTemp.p.get(0);
					// keyTemp = keyTemp.p.get(0);
					// p.get(i-1).p.add(keyTemp);
					// p.get(i-1).p.add(p.get(i+1).p.remove(0));
					// p.get(i+1).p.remove(0);
					// p.get(i+1).key = p.get(i+1).p.get(0).key;
					// }
					// }
					// else {
					// if(p.get(i-3).p.size() <= 2 * maxSize - 1) {
					// Node keyTemp = p.get(i-3).p.get(p.get(i-3).IndexOf(Node.BIGGEST_SEARCH));
					// while(keyTemp.typeOfNode == Node.NON_LEAF_NODE) keyTemp = keyTemp.p.get(0);
					// keyTemp = keyTemp.p.get(0);
					// p.get(i-3).p.add(keyTemp);
					// p.get(i-3).p.add(p.get(i-1).p.remove(0));
					// p.remove(i-1);
					// p.remove(i-2);
					// }
					// else {
					// Node keyTemp = p.get(i-1).p.get(0);
					// while(keyTemp.typeOfNode == Node.NON_LEAF_NODE) keyTemp = keyTemp.p.get(0);
					// keyTemp = keyTemp.p.get(0);
					//
					// p.get(i-1).p.add(0,keyTemp);
					// p.get(i-1).p.add(0,p.get(i-3).p.remove(p.get(i-3).IndexOf(Node.BIGGEST_SEARCH)));
					// p.get(i-3).p.remove(p.get(i-3).IndexOf(Node.BIGGEST_SEARCH));
					// p.get(i-1).key = p.get(i-1).p.get(0).key;
					// }
					// }
//				}
//			}

			return true;
		} else {
			System.out.println("[" + this.key + "]Delete " + node.key + " Failed!");
		}
		return false;
	}
}
