import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class BTree<T extends Sortable> {
	public static final int DATA_NODE_CAPACITY = 20;
	public static final int INDEX_NODE_BRANCH_FACTOR = 50;

	private Node<T> root;

	/**
	 * Construct an empty tree
	 */
	public BTree() {
		root = new IndexNode<T>();
	}

	/**
	 * return the string produced by an inorder traversal of this tree
	 */
	@Override
	public String toString() {
		return root.toString();
	}

	// /**
	// * This one asks for more info from each node. You can write it like the
	// * arraylist-based toString() method from the BST assignment. However, the
	// * output isn't just the elements, but the elements, ranks, and balance
	// * codes. Former CSSE230 students recommended that this method, while
	// making
	// * it harder to pass tests initially, saves them time later since it
	// catches
	// * weird errors that occur when you don't update ranks and balance codes
	// * correctly. For the tree with node b and children a and c, it should
	// * return the string: [b1=, a0=, c0=] There are many more examples in the
	// * unit tests.
	// *
	// * @return The string of elements, ranks, and balance codes, given in a
	// * pre-order traversal of the tree.
	// */
	// public String toDebugString() {
	// if (root == Node.NULL_NODE)
	// return "[]";
	// String s = "[" + root.toDebugString();
	// return s.substring(0, s.length() - 2) + "]";
	// }

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
	public T fetch(int identifier) {
		return root.fetch(identifier);
	}

	public int remove(int identifier) {
		Node<?> ret = root.remove(identifier);
		if (ret == null) // no such element, remove fail
			return 0;
		root = ret;
		return 1;
	}

	/**
	 * add an element to the end of this tree
	 * 
	 * @param c
	 *            character to add to the end of this tree.
	 */
	public void add(int identifier, T data) {
		Node<T> ret = root.add(identifier, data);
		if (ret != null) {
			List<Node<T>> node = new ArrayList<>();
			List<Integer> ids = new ArrayList<>();
			node.add(root);
			node.add(ret);
			ids.add(ret.getFirstIdentify());
			root = new IndexNode<T>(node, ids);
		}
	}

	private interface Node<T> {

		/**
		 * 
		 * @param identifier
		 * @param data
		 * @return the split page not if there is a page split, null if no split
		 *         happened
		 */
		public Node<T> add(int identifier, T data);

		/**
		 * fetch the data according to the identifier
		 * 
		 * @param identifier
		 * @return the data entry found, or null if no such data entry found
		 */
		public T fetch(int identifier);

		/**
		 * 
		 * @param identifier
		 * @return
		 */
		public Node<T> remove(int identifier);

		public boolean isEmpty();

		public boolean notHalfFull();

		public boolean isFull();

		public int size();

		public int height();

		public Integer getFirstIdentify();
	}

	private class IndexNode<T> implements Node<T> {
		private ArrayList<Node<T>> pointers = new ArrayList<Node<T>>(INDEX_NODE_BRANCH_FACTOR);
		private ArrayList<Integer> identity = new ArrayList<Integer>(INDEX_NODE_BRANCH_FACTOR - 1);

		public IndexNode() {

		}

		public IndexNode(List<Node<T>> pointers, List<Integer> identity) {
			for (Node<T> i : pointers)
				this.pointers.add(i);
			for (Integer i : identity)
				this.identity.add(i);
		}

		@Override
		public Node<T> add(int identifier, T data) {
			int low = 0;
			int up = identity.size();
			while (up - low > 1) {
				int mid = (low + up) / 2;
				if (identifier < identity.get(mid)) {
					up = mid;
				} else {
					low = mid;
				}
			}
			Node<T> ret = pointers.get(up).add(identifier, data);
			if (ret != null) {
				pointers.add(up + 1, ret);
				identity.add(up, ret.getFirstIdentify());
				if (size() > INDEX_NODE_BRANCH_FACTOR) {
					int div = (pointers.size() + 1) / 2;
					Node<T> splited = new IndexNode<T>(pointers.subList(div, pointers.size()),
							identity.subList(div, identity.size()));
					pointers.subList(div, pointers.size()).clear();
					identity.subList(div - 1, identity.size()).clear();
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

		@Override
		public Node<T> remove(int identifier) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean isFull() {
			return pointers.size() == INDEX_NODE_BRANCH_FACTOR;
		}

		@Override
		public int size() {
			return 0;
		}

		@Override
		public int height() {
			return pointers.get(0).height() + 1;
		}

		@Override
		public boolean isEmpty() {
			return pointers.size() == 0;
		}

		@Override
		public boolean notHalfFull() {
			return pointers.size() * 2 < INDEX_NODE_BRANCH_FACTOR;
		}

		@Override
		public T fetch(int identifier) {
			int low = 0;
			int up = identity.size();
			while (up - low > 1) {
				int mid = (low + up) / 2;
				if (identifier < identity.get(mid)) {
					up = mid;
				} else {
					low = mid;
				}
			}
			return pointers.get(up).fetch(identifier);
		}

		@Override
		public Integer getFirstIdentify() {
			return pointers.get(0).getFirstIdentify();
		}

	}

	private class DataNode<T> implements Node<T> {
		private ArrayList<T> data = new ArrayList<>(DATA_NODE_CAPACITY);
		private ArrayList<Integer> identity = new ArrayList<>(DATA_NODE_CAPACITY);

		public DataNode() {
		}

		public DataNode(List<T> data, List<Integer> identity) {
			for (T i : data)
				this.data.add(i);
			for (Integer i : identity)
				this.identity.add(i);
		}

		@Override
		public Node<T> add(int identifier, T newData) {
			int low = 0;
			int up = identity.size();
			while (up - low > 1) {
				int mid = (low + up) / 2;
				if (identifier < identity.get(mid)) {
					up = mid;
				} else {
					low = mid;
				}
			}
			data.add(up, newData);
			identity.add(up, identifier);
			if (size() > DATA_NODE_CAPACITY) {
				int div = (data.size() + 1) / 2;
				Node<T> splited = new DataNode<T>(data.subList(div, data.size()),
						identity.subList(div, identity.size()));
				data.subList(div, data.size()).clear();
				identity.subList(div, identity.size()).clear();
				return splited;
			}
			return null;
		}

		@Override
		public Node<T> remove(int identifier) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean isFull() {
			return identity.size() == DATA_NODE_CAPACITY;
		}

		@Override
		public int size() {
			return identity.size();
		}

		@Override
		public int height() {
			return 0;
		}

		@Override
		public boolean isEmpty() {
			return identity.size() == 0;
		}

		@Override
		public boolean notHalfFull() {
			return false;
		}

		@Override
		public T fetch(int identifier) {
			int low = 0;
			int up = identity.size();
			while (up - low > 1) {
				int mid = (low + up) / 2;
				if (identifier < identity.get(mid)) {
					up = mid;
				} else {
					low = mid;
				}
			}
			if (identity.get(low) == identifier)
				return data.get(low);
			return null;
		}

		@Override
		public Integer getFirstIdentify() {
			return identity.get(0);
		}

	}

}
