package imageprocessing;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class WeightedQuickUnionUFTest {

	private WeightedQuickUnionUF uf;

	@Before
	public void setUp() throws Exception {
		uf = new WeightedQuickUnionUF(10);
	}

	@After
	public void tearDown() throws Exception {
		uf = null;
	}

	@Test
	public void testWeightedQuickUnionUF() {

		// Test that on intialisation each element has a unique root
		// i.e. Check that no two elements have the same root
		// Type of test: Reference
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				if (i != j) {
					assertNotEquals(uf.root(i), uf.root(j));
				}
			}
		}
	}

	@Test
	public void testCount() {

		// Test that count returns the number of components
		// Type of test: Cardinality
		assertEquals(10, uf.count());

		for (int i = 1; i < 10; i++) {
			uf.union(i - 1, i);
			assertEquals(10 - i, uf.count());
		}
	}

	@Test
	public void testRoot() {
		// Test that root returns the root of elements
		// Type of test: Right
		for (int i = 0; i < 10; i++) {
			assertEquals(i, uf.root(i));
		}

	}

	@Test
	public void testConnected() {
		// Test that connect verifies if two elements are verified
		// Type of test: Right
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				if (i != j) {
					assertFalse(uf.connected(i, j));
				}
			}
		}

		uf.union(0, 1);
		assertTrue(uf.connected(0, 1));
		uf.union(2, 3);
		assertTrue(uf.connected(2, 3));
		assertFalse(uf.connected(1, 2));
	}

	@Test
	public void testUnion() {
		// Test performing a union between two elements
		// Type of test: Right
		uf.union(0, 1);
		assertTrue(uf.connected(0, 1));
		// Type of test: Inverse
		assertEquals(uf.root(0), uf.root(1));

	}

	@Test
	public void testConnectivityProperties() {
		// Test reflexive Property of connectivity
		// Type of test: Right
		for (int i = 0; i < 10; i++) {
			assertTrue(uf.connected(i, i));
		}

		// Test symmetric property of connectivity
		// Type of test: Inverse
		uf.union(0, 1);
		assertTrue(uf.connected(0, 1));
		assertTrue(uf.connected(1, 0));

		// Test transitive property of connectivity
		// Type of test: Right
		uf.union(2, 3);
		assertTrue(uf.connected(2, 3));
		assertFalse(uf.connected(1, 2));
		uf.union(1, 3);
		for (int i = 1; i < 4; i++) {
			assertTrue(uf.connected(0, i));
		}
	}
}
