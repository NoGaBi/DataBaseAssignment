package bPlusTree;

public class Value_Node extends Node {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1878172095545722000L;
	protected int value;
		
	public Value_Node(int key, int value) {
		this.typeOfNode = -1;
		this.key = key;
		this.value = value;
		this.active = true;
	}

	public int getKey() {
		return this.key;
	}
	public int getValue() {
		return this.value;
	}
	
	public void Insert(Node node) {
		return;
	}

	@Override
	public Node Search(int key) {
		return this;
	}

	@Override
	public int IndexOf(int type) {
		return -1;
	}
	
	@Override
	public String toString() {
		return this.key + "," + this.value;
	}
	
	@Override
	public boolean Delete(Node node) {
		if(this.key == node.key) {
			if(this.r != null) this.r.l = this.l;
			if(this.l != null) this.l.r = this.r;
			return true;
		}
		return false;
	}
}
