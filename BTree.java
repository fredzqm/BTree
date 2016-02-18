import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class BTree<T extends Identifiable> {
	public static final int DATA_NODE_CAPACITY = 5;
	public static final int INDEX_NODE_BRANCH_FACTOR = 5;

	private Node<T> root;

	/**
	 * Construct an empty tree Empty BTree has a null root
	 */
	public BTree() {
		root = null;
	}

	/**
	 * return the string produced by an inorder traversal of this tree
	 */
	@Override
	public String toString() {
		return "Tree root: \n" + root.toStringHelper("");
	}

	/**
	 * 
	 * @return the height of this tree
	 */
	public int height() {
		return root.height();
	}

	/**
	 * 
	 * @return the number of nodes in this tree
	 */
	public int size() {
		return root.size();
	}

	/**
	 * find the index of the first match substring inside this editor tree.
	 * 
	 * @param s
	 *            the string to look for
	 * @return the positiovgfn in this tree of the first occurrence of s; -1 if
	 *         s does not occur
	 */
	// public T fetch(int identifier) {
	// return root.fetch(identifier);
	// }

	// public int remove(int identifier) {
	// Node<?> ret = root.remove(identifier);
	// if (ret == null) // no such element, remove fail
	// return 0;
	//// root = ret;
	// return 1;
	// }

	/**
	 * add an element to the end of this tree
	 * 
	 * @param c
	 *            character to add to the end of this tree.
	 */
	public void add(T data) {
		if (root == null) {
			Node<T> firstDataNode = new DataNode<T>(data);
			root = new IndexNode<T>(firstDataNode);
			return;
		}
		Node<T> ret = root.add(data);
		if (ret != null) {
			// this level is no longer enough, grow another level
			IndexNode<T> newRoot = new IndexNode<T>(root);
			newRoot.appendNode(ret);
			root = newRoot;
		}
	}

	private abstract class Node<T extends Identifiable> extends Identifiable {
		protected int lowerID;

		/**
		 * 
		 * @param data
		 * @return the split page not if there is a page split, null if no split
		 *         happened
		 */
		public abstract Node<T> add(T data);

		/**
		 * fetch the data according to the identifier
		 * 
		 * @param identifier
		 * @return the data entry found, or null if no such data entry found
		 */
		// public T fetch(int identifier);

		/**
		 * 
		 * @param identifier
		 * @return
		 */
		// public Node<T> remove(int identifier);

//		public abstract boolean isEmpty();

		public abstract boolean notHalfFull();

//		public abstract boolean isFull();

		public abstract int size();

		public abstract int height();

		public int getIdentifier() {
			return lowerID;
		}
		
		public abstract String toStringHelper(String prefix);
		
		public String toString(){
			return toStringHelper("");
		}
		
	}

	private class IndexNode<T extends Identifiable> extends Node<T> {
		private ArrayList<Node<T>> p;

		/**
		 * create an index node given the smallest node.
		 * 
		 * @param smallestElement
		 */
		public IndexNode(Node<T> smallestElement) {
			p = new ArrayList<Node<T>>(INDEX_NODE_BRANCH_FACTOR);
			p.add(smallestElement);
			this.lowerID = smallestElement.lowerID;
		}

		public IndexNode(List<Node<T>> list) {
			p.addAll(list);
			this.lowerID = p.get(0).lowerID;
		}

		public void appendNode(Node<T> node) {
			p.add(node);
		}

		@Override
		public Node<T> add(T data) {
			int low = 0;
			int up = p.size() - 1;
			Node<T> ret;
			if (data.compareToIdentifier(this.lowerID) < 0) {
				this.lowerID = data.getIdentifier();
				ret = p.get(0).add(data);
			} else {
				while (up - low > 1) {
					int mid = (low + up) / 2;
					if (p.get(mid).compareTo(data) < 0) {
						low = mid;
					} else {
						up = mid;
					}
				}
				ret = p.get(up).add(data);
			}
			// after this loop p.get(low) should be the right node to add
			if (ret != null) {
				p.add(up + 1, ret);
				if (size() > INDEX_NODE_BRANCH_FACTOR) {
					int div = (p.size() + 1) / 2;
					Node<T> splited = new IndexNode<T>(p.subList(div, p.size()));
					p.subList(div, p.size()).clear();
					return splited;
				}
			}
			return null;
		}

		/**
		 * When a node is already full but a page split occurs in its children
		 * node, this node needs to be split into two nodes.
		 * 
		 * This method splits the current node with an addition node inserted at
		 * index. After it returns, the old node will contain the left split
		 * node and the right split node will be returned.
		 * 
		 * @param index
		 *            the index of the additional node
		 * @param node
		 *            the additional node
		 * @return splited node
		 */
		// private Node<T> split() {
		//
		// }

		// @Override
		// public Node<T> remove(int identifier) {
		// // TODO Auto-generated method stub
		// return null;
		// }

//		@Override
//		public boolean isFull() {
//			return p.size() == INDEX_NODE_BRANCH_FACTOR;
//		}

		@Override
		public int size() {
			return 0;
		}

		@Override
		public int height() {
			return p.get(0).height() + 1;
		}

//		@Override
//		public boolean isEmpty() {
//			return p.size() == 0;
//		}

		@Override
		public boolean notHalfFull() {
			return p.size() * 2 < INDEX_NODE_BRANCH_FACTOR;
		}
		
		public String toStringHelper(String prefix){
			StringBuilder sb = new StringBuilder();
			sb.append(prefix + "<INode("+prefix.length()+") least=" + this.lowerID +">\n");
			for (Node<T> n : p){
				sb.append(n.toStringHelper(prefix + " "));
			}
			sb.append(prefix + "</INode("+prefix.length()+")>\n" );
			return sb.toString();
		}
	}

	private class DataNode<T extends Identifiable> extends Node<T> {
		private ArrayList<T> d;

		public DataNode(T firstData) {
			d = new ArrayList<>(DATA_NODE_CAPACITY);
			d.add(firstData);
		}

		public DataNode(List<T> data) {
			d = new ArrayList<>(DATA_NODE_CAPACITY);
			d.addAll(data);
		}

		@Override
		public Node<T> add(T data) {
			int low = 0;
			int up = d.size();
			if (data.compareToIdentifier(this.lowerID) < 0) {
				this.lowerID = data.getIdentifier();
				d.add(0, data);
			} else {
				while (up - low > 1) {
					int mid = (low + up) / 2;
					if (d.get(mid).compareTo(data) < 0) {
						low = mid;
					} else {
						up = mid;
					}
				}
				d.add(up, data);
			}
			if (size() > DATA_NODE_CAPACITY) {
				int div = (d.size() + 1) / 2;
				DataNode<T> splited = new DataNode<T>(d.subList(div, d.size()));
				d.subList(div, d.size()).clear();
				return splited;
			}
			return null;
		}

		// @Override
		// public Node<T> remove(int identifier) {
		// // TODO Auto-generated method stub
		// return null;
		// }

		@Override
		public int size() {
			return d.size();
		}

		@Override
		public int height() {
			return 0;
		}
		
		@Override
		public boolean notHalfFull() {
			return false;
		}

		@Override
		public String toStringHelper(String prefix) {
			StringBuilder sb = new StringBuilder();
			sb.append(prefix + "<DNode("+prefix.length()+") ");
			sb.append(d.toString());
			sb.append(" />\n" );
			return sb.toString();
		}

//		@Override
//		public T fetch(int identifier) {
//			int low = 0;
//			int up = identity.size();
//			while (up - low > 1) {
//				int mid = (low + up) / 2;
//				if (identifier < identity.get(mid)) {
//					up = mid;
//				} else {
//					low = mid;
//				}
//			}
//			if (identity.get(low) == identifier)
//				return data.get(low);
//			return null;
//		}

	}

}
