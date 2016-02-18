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
	public void test() {
		BTree tree = new BTree<Int>();
		tree.add(new Int(1));
		System.out.println(tree);
		tree.add(new Int(2));
		System.out.println(tree);
		tree.add(new Int(3));
		System.out.println(tree);
		tree.add(new Int(4));
		System.out.println(tree);
	}

}

class Int extends Identifiable{
	int i;
	public Int(int j) {
		i = j;
	}
	@Override
	public int getIdentifier() {
		return i;
	}
	
	public String toString(){
		return ""+i;
	}
	
}