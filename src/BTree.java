import java.util.ArrayList;
import java.util.List;

public class BTree<T extends Identifiable> {
	public static final int DATA_NODE_CAPACITY = 5;
	public static final int INDEX_NODE_BRANCH_FACTOR = 5;

	private IndexNode root;

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
		if (root == null)
			return "<>";
		return "Tree root: \n" + root.toStringHelper("", 0);
	}

	public boolean isEmpty() {
		return root == null;
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
	public T fetch(int identifier) {
		return root.fetch(identifier);
	}

	public T remove(int identifier) {
		if (root == null)
			return null;
		T ret = root.remove(identifier);
		if (root.size() == 1) {
			if (root.p.get(0).getClass() == DataNode.class) {
				if (root.p.get(0).size() == 0)
					root = null;// this Btress is empty now.
			} else if (root.p.get(0).getClass() == IndexNode.class) {
				root = (IndexNode) root.p.get(0); // reduce the level of our
														// BTree
			}
		}
		return ret;
	}

	/**
	 * add an element to the end of this tree
	 * 
	 * @param c
	 *            character to add to the end of this tree.
	 */
	public void add(T data) {
		if (root == null) {
			Node firstDataNode = new DataNode(data);
			root = new IndexNode(firstDataNode);
			return;
		}
		Node ret = root.add(data);
		if (ret != null) {
			// this level is no longer enough, grow another level
			IndexNode newRoot = new IndexNode(root);
			newRoot.p.add(ret);
			root = newRoot;
		}
	}

	private abstract class Node extends Identifiable {
		protected int lowerID;

		/**
		 * 
		 * @param data
		 * @return the split page not if there is a page split, null if no split
		 *         happened
		 */
		public abstract Node add(T data);

		/**
		 * fetch the data according to the identifier
		 * 
		 * @param identifier
		 * @return the data entry found, or null if no such data entry found
		 */
		public abstract T fetch(int identifier);

		/**
		 * 
		 * @param identifier
		 * @return
		 */
		public abstract T remove(int identifier);

		public abstract boolean mergeWith(Node remove);

		public abstract boolean notHalfFull();

		public abstract int size();

		public abstract int height();

		public int getIdentifier() {
			return lowerID;
		}

		public abstract String toStringHelper(String prefix, int index);

		public String toString() {
			return toStringHelper("", 0);
		}

	}

	private class IndexNode extends Node {
		private ArrayList<Node> p;

		/**
		 * create an index node given the smallest node.
		 * 
		 * @param smallestElement
		 */
		public IndexNode(Node smallestElement) {
			p = new ArrayList<Node>(INDEX_NODE_BRANCH_FACTOR);
			p.add(smallestElement);
			this.lowerID = smallestElement.lowerID;
		}

		public IndexNode(List<Node> list) {
			p = new ArrayList<Node>(INDEX_NODE_BRANCH_FACTOR);
			p.addAll(list);
			this.lowerID = p.get(0).lowerID;
		}

		@Override
		public Node add(T data) {
			int low = 0;
			int up = p.size();
			Node ret;
			if (data.compareToIdentifier(this.lowerID) < 0) {
				this.lowerID = data.getIdentifier();
				ret = p.get(0).add(data);
			} else {
				while (up - low > 1) {
					int mid = (low + up) / 2;
					if (p.get(mid).compareTo(data) <= 0) {
						low = mid;
					} else {
						up = mid;
					}
				}
				ret = p.get(low).add(data);
			}
			// after this loop p.get(low) should be the right node to add
			if (ret != null) {
				p.add(low + 1, ret);
				if (size() > INDEX_NODE_BRANCH_FACTOR) {
					int div = (p.size() + 1) / 2;
					Node splited = new IndexNode(p.subList(div, p.size()));
					p.subList(div, p.size()).clear();
					return splited;
				}
			}
			return null;
		}

		@Override
		public T fetch(int identifier) {
			if (identifier < this.lowerID) {
				return null;
			}
			int low = 0;
			int up = p.size();
			while (up - low > 1) {
				int mid = (low + up) / 2;
				if (p.get(mid).compareToIdentifier(identifier) <= 0) {
					low = mid;
				} else {
					up = mid;
				}
			}
			return p.get(low).fetch(identifier);
		}

		@Override
		public T remove(int identifier) {
			if (identifier < this.lowerID) {
				return null;
			}
			int low = 0;
			int up = p.size();
			while (up - low > 1) {
				int mid = (low + up) / 2;
				if (p.get(mid).compareToIdentifier(identifier) <= 0) {
					low = mid;
				} else {
					up = mid;
				}
			}
			T ret = p.get(low).remove(identifier);
			int newSize = p.get(low).size();
			if (newSize * 2 < INDEX_NODE_BRANCH_FACTOR) {
				if (this.size() == 1) {
					// nothing can be done here.
				} else if (low == 0) {
					if (p.get(low).mergeWith(p.get(low + 1)))
						p.remove(low + 1);
				} else if (low == this.size() - 1) {
					if (p.get(low - 1).mergeWith(p.get(low)))
						p.remove(low);
				} else {
					if (p.get(low - 1).size() < p.get(low + 1).size()) {
						if (p.get(low).mergeWith(p.get(low + 1)))
							p.remove(low + 1);
					} else {
						if (p.get(low - 1).mergeWith(p.get(low)))
							p.remove(low);
					}
				}
			}
			return ret;
		}

		/**
		 * 
		 * @param node
		 * @return true when two nodes are merged and node become empty
		 *         afterwards, false when they are only balanced
		 */
		public boolean mergeWith(Node node) {
			IndexNode indexNode = (IndexNode) node;
			this.p.addAll(indexNode.p);
			indexNode.p.clear();
			if (p.size() <= INDEX_NODE_BRANCH_FACTOR)
				return true;
			int div = (p.size() + 1) / 2;
			indexNode.p.addAll(p.subList(div, p.size()));
			p.subList(div, p.size()).clear();
			indexNode.lowerID = indexNode.p.get(0).getIdentifier();
			return false;
		}

		@Override
		public int height() {
			return p.get(0).height() + 1;
		}

		@Override
		public int size() {
			return p.size();
		}

		@Override
		public boolean notHalfFull() {
			return p.size() * 2 < INDEX_NODE_BRANCH_FACTOR;
		}

		public String toStringHelper(String prefix, int index) {
			StringBuilder sb = new StringBuilder();
			sb.append(prefix + "<INode(" + prefix.length() + ")[" + index + "] least=" + this.lowerID + ">\n");
			for (int i = 0; i < p.size(); i++) {
				sb.append(p.get(i).toStringHelper(prefix + " ", i));
			}
			sb.append(prefix + "</INode(" + prefix.length() + ")>\n");
			return sb.toString();
		}

	}

	private class DataNode extends Node {
		private ArrayList<T> d;

		public DataNode(T firstData) {
			d = new ArrayList<>(DATA_NODE_CAPACITY);
			d.add(firstData);
			this.lowerID = firstData.getIdentifier();
		}

		public DataNode(List<T> data) {
			d = new ArrayList<>(DATA_NODE_CAPACITY);
			d.addAll(data);
			this.lowerID = d.get(0).getIdentifier();
		}

		@Override
		public Node add(T data) {
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
				DataNode splited = new DataNode(d.subList(div, d.size()));
				d.subList(div, d.size()).clear();
				return splited;
			}
			return null;
		}

		@Override
		public T fetch(int identifier) {
			if (identifier < this.lowerID) {
				return null;
			}
			int low = 0;
			int up = d.size();
			while (up >= low) {
				int mid = (low + up) / 2;
				if (d.get(mid).compareToIdentifier(identifier) == 0) {
					return d.get(0);
				} else if (d.get(mid).compareToIdentifier(identifier) < 0) {
					low = mid + 1;
				} else {
					up = mid - 1;
				}
			}
			return null;
		}

		@Override
		public T remove(int identifier) {
			if (identifier < this.lowerID) {
				return null;
			}
			int low = 0;
			int up = d.size();
			while (up >= low) {
				int mid = (low + up) / 2;
				if (d.get(mid).compareToIdentifier(identifier) == 0) {
					return d.remove(mid);
				} else if (d.get(mid).compareToIdentifier(identifier) < 0) {
					low = mid + 1;
				} else {
					up = mid - 1;
				}
			}
			return null;
		}

		public boolean mergeWith(Node node) {
			DataNode dataNode = (DataNode) node;
			this.d.addAll(dataNode.d);
			dataNode.d.clear();
			if (d.size() <= INDEX_NODE_BRANCH_FACTOR)
				return true;
			int div = (d.size() + 1) / 2;
			dataNode.d.addAll(d.subList(div, d.size()));
			d.subList(div, d.size()).clear();
			dataNode.lowerID = dataNode.d.get(0).getIdentifier();
			return false;
		}

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
			return d.size() * 2 < DATA_NODE_CAPACITY;
		}

		@Override
		public String toStringHelper(String prefix, int index) {
			StringBuilder sb = new StringBuilder();
			sb.append(prefix + "<DNode(" + prefix.length() + ")[" + index + "]");
			sb.append(d.toString());
			sb.append(" />\n");
			return sb.toString();
		}

	}

}
