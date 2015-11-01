package imageprocessing;

public class WeightedQuickUnionUF {
	private int[] id; // id[i] = component identifier of i
	private int[] sz; // sz[i] = number of objects in tree rooted at i
	private int count; // number of distinct roots

	/**
	 * Initializes an empty union-find data structure with N sites
	 */
	public WeightedQuickUnionUF(int N) {
		id = new int[N];
		for (int i = 0; i < N; i++) {
			id[i] = i;
		}
		sz = new int[N];
		for (int i = 0; i < N; i++) {
			sz[i] = 1;
		}
		count = N;
	}

	/**
	 * Returns the number of components.
	 */
	public int count() {
		return count;
	}

	/**
	 * Returns the root of object
	 */
	public int root(int i) {
		while (i != id[i]) {
			id[i] = id[id[i]];	// path compression
			i = id[i];
		}
		return i;
	}

	/**
	 * Returns true if the the two sites are in the same component.
	 */
	public boolean connected(int p, int q) {
		return (root(p) == root(q));
	}

	/**
	 * Merges the component containing site p with the the component containing
	 * site q.
	 */
	public void union(int p, int q) {
		int i = root(p), j = root(q);
		if (sz[i] <= sz[j]) {
			id[i] = j;
			sz[j] += sz[i];
		} else {
			id[j] = i;
			sz[i] += sz[j];
		}
		count--;
	}
}