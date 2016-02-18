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
		for (int i = 0 ; i < 100; i++){
			tree.add(new Int((int)(Math.random()*100)));
		}
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