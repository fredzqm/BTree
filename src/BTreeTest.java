import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class BTreeTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testInsert() {
		BTree<Int> tree = new BTree<Int>();
		for (int i = 0; i < 100; i++) {
			tree.add(new Int(i));
		}
		assertEquals(new Int(3), tree.fetch(3));
	}

	@Test
	public void testRandomInsert() {
		BTree<Int> tree = new BTree<Int>();
		for (int i = 0; i < 100; i++) {
			tree.add(new Int((int) (Math.random() * 100)));
		}
		System.out.println(tree);
	}

	@Test
	public void testRemove() {
		BTree<Int> tree = new BTree<Int>();
		for (int i = 0; i < 100; i++) {
			tree.add(new Int(i));
		}
		tree.remove(2);
		// tree.remove(8);
		System.out.println(tree);
		// System.out.println(tree.fetch(3));
	}

	@Test
	public void testRemoveDecreaseLevel() {
		BTree<Int> tree = new BTree<Int>();
		for (int i = 0; i < 10; i++) {
			tree.add(new Int(i));
		}
		tree.remove(2);
		tree.remove(8);
		tree.remove(9);
		tree.remove(5);
		tree.remove(1);
		tree.remove(3);
		tree.remove(4);
		tree.remove(6);
		tree.remove(0);
		tree.remove(7);
		assertTrue(tree.isEmpty());
		// System.out.println(tree.fetch(3));
	}
}

class Int extends Identifiable {
	private int i;

	public Int(int j) {
		i = j;
	}

	@Override
	public int getIdentifier() {
		return i;
	}

	public String toString() {
		return "" + i;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Int) {
			Int x = (Int) obj;
			return x.i == this.i;
		}
		return false;
	}
}