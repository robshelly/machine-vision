package imageprocessing;

import java.awt.Color;
import java.util.Arrays;
import java.util.HashMap;

import edu.princeton.cs.introcs.Picture;
import edu.princeton.cs.introcs.StdOut;

/*************************************************************************
 * Compilation: javac ConnectedComponentImage.java
 * 
 * The <tt>ConnectedComponentImage</tt> class
 * <p>
 * You do the rest....
 * 
 * @author
 *************************************************************************/
public class ComponentImage {

	private Picture pic; // picture to analyse
	private HashMap<Integer, Color> roots;  // Maps roots to colors
											// Serves two purposes:
											// using map.keySet(), acts as a set of roots
											// each corresponding to  component in the image
											// also, associates a colour with each root
	private WeightedQuickUnionUF uf; // instance of union find class
	private int w; // width of pic
	private int h; // height of pic
	private int threshold;	//a value from 0 - 255 that defines the threshold for binarisation

	private boolean connected; // flag that indicates if image has been analysed
								// yet
	private boolean rootsSetBuilt; // flag that indicates if set of roots has
									// been built

	/**
	 * Constructor for Component Image
	 * Uses a String for a file location
	 * to initialise the pic field
	 * 
	 * @param fileLocation String containing the filepath of the image
	 */
	public ComponentImage(String fileLocation) {
		pic = new Picture(fileLocation);
		threshold = 128;
		initialise();
	}
	
	/**
	 * Alternate Constructor for Component Image
	 * Uses a Picture objet as a parameter
	 * to initialise the pic field
	 * 
	 * @param picture Picture object the image
	 */
	public ComponentImage(Picture picture) {
		pic = picture;
		threshold = 128;
		initialise();
	}

	/**
	 * Returns the original picture
	 * 
	 * @return a Picture object
	 */
	public Picture getPicture() {
		return pic;
	}
	
	/**
	 * Returns a binarised version of the original image
	 * 
	 * @return a picture object with all components surrounded by a red box
	 */
	public Picture binaryComponentImage() {

		Picture binaryPic = new Picture(pic);

		// convert to grayscale
		for (int x = 0; x < binaryPic.width(); x++) {
			for (int y = 0; y < binaryPic.height(); y++) {
				Color color = binaryPic.get(x, y);
				Color gray = Luminance.toGray(color);
				binaryPic.set(x, y, gray);

			}
		}

		// convert to binary

		for (int x = 0; x < binaryPic.width(); x++) {
			for (int y = 0; y < binaryPic.height(); y++) {
				Color c = binaryPic.get(x, y);
				if (Luminance.lum(c) < threshold) {
					binaryPic.set(x, y, Color.BLACK);
				} else {
					binaryPic.set(x, y, Color.WHITE);
				}
			}
		}
		StdOut.println("Binarising Image");
		return binaryPic;
	}
	
	/**
	 * Returns the number of components identified in the image.
	 * 
	 * @return the number of components (between 1 and N)
	 */
	public int countComponents() {

		// Use union find algorithms to connect pixels
		// into object before continuing
		if (!connected) {
			//connectComponents();
			connectComponentsOnePass();
		}

		// To return object count, deduct 1 from ufs count
		// because uf has counted the background as a component
		return (uf.count() - 1);
	}

	/**
	 * Returns a picture with each object updated to a random colour.
	 * 
	 * @return a picture object with all components coloured.
	 */
	public Picture colourComponentImage() {

		//TODO Fix this, image is sometimes being binarised twice
		Picture colourPic = new Picture(binaryComponentImage());

		// Set of roots for objects needed before proceeding
		if (!rootsSetBuilt) {
			buildRootsSet();
		}

		// Iterate through pixels
		// If root is not zero (i.e. not a background pixel)
		// Use roots map to get a colour from roots
		// Set pixel to that colour
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				int i = uf.root(y * w + x);
				if (i != 0) {
					colourPic.set(x, y, roots.get(i));
				}
			}
		}

		return colourPic;

	}

	
	/**
	 * Returns an image with all analysed objects surrounded by a red box
	 * Method takes in an image as a paramter and returns this image
	 * with the red boxes drawn on it
	 * 
	 * This allows the user to choose which type of image they would
	 * like to view in the highlighted mode i.e. the original picture
	 * a binary picture or a coloured objects picture
	 * 
	 * @param pic The image to be highlighted
	 * @return The image with the objects highlighted
	 */
	public Picture highlightComponentImage(Picture pic) {
		
		//create copy of image in parameter
		//This avoid overwriting the global field pic
		//if the user chooses to use the original image as the parameter
		Picture highlightedPic = new Picture(pic);

		//TODO Testing
		//Picture highlightedPic = new Picture(binaryComponentImage());
		//Picture highlightedPic = new Picture(pic);

		// Set of roots for objects needed before proceeding
		if (!rootsSetBuilt) {
			buildRootsSet();
		}

		// Arrays will hold the lines that define component boundaries
		int[] lowerYValues = new int[roots.size()];
		Arrays.fill(lowerYValues, pic.height());
		int[] lowerXValues = new int[roots.size()];
		Arrays.fill(lowerXValues, pic.width());
		int[] upperYValues = new int[roots.size()];
		Arrays.fill(upperYValues, 0);
		int[] upperXValues = new int[roots.size()];
		Arrays.fill(upperXValues, 0);

		// Map each component via its associated root
		// to an index in the arrays
		// Thus, each component will have all it's boundaries
		// stored in the same index in each array.
		HashMap<Integer, Integer> componentIndex = new HashMap<Integer, Integer>();
		int index = 0;
		for (Integer root : roots.keySet()) {
			componentIndex.put(root, index);
			index++;
			// StdOut.println("Component with root " + root + " has index " +
			// index);
		}

		// Set boundary components for every component in one pass
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {

				// check pixel not background
				if (uf.root(y * w + x) != 0) {

					int i = componentIndex.get(uf.root(y * w + x));

					// Subtract one from lower boundaries
					// Add one to upper boundaries
					// This ensures boxes fully contain objects
					// i.e. they don't overlap the edges
					// TODO Implement the above but add validation
					// to avoid out of bounds exceptions
					if (x <= lowerXValues[i])
						lowerXValues[i] = x - 1;
					if (x >= upperXValues[i])
						upperXValues[i] = x + 1;
					if (y <= lowerYValues[i])
						lowerYValues[i] = y - 1;
					if (y >= upperYValues[i])
						upperYValues[i] = y + 1;
					
					//bad version
//					if (x <= lowerXValues[i])
//						lowerXValues[i] = x;
//					if (x >= upperXValues[i])
//						upperXValues[i] = x;
//					if (y <= lowerYValues[i])
//						lowerYValues[i] = y;
//					if (y >= upperYValues[i])
//						upperYValues[i] = y;
				}

			}
		}

		// draw lines for each component
		for (Integer root : roots.keySet()) {

			int i = componentIndex.get(root);
			highlightedPic = drawBoundary(highlightedPic, lowerYValues[i],
					lowerXValues[i], upperYValues[i], upperXValues[i]);
		}

		return highlightedPic;
	}

	/**
	 * Returns the threshold for image binarisation
	 * 
	 * @return an integer in the range of 0 to 255
	 */
	public int getThreshold() {
		return threshold;
	}
	
	/**
	 * Set the threshold for image binarisation
	 * 
	 * Note: Changing the threshold may change the number of
	 * objects in the image when binarised
	 * Therefore this method calls the initialise method
	 * to reset all fields
	 * 
	 * @param threshold and int in the range of o to 255
	 */
	public void setThreshold(int threshold) {
		if (threshold < 0) {
			this.threshold = 0;
		} else if (threshold > 255) {
			this.threshold = 255;
		} else {
			this.threshold = threshold;
		}
		initialise();
	}
		
	/**
	 * Performs raster scan of image Connects pixels of equal colour using
	 * union-find algorithm
	 * Builds a count of the number of components that comprise the image
	 */
	private void connectComponents() {
		
		// Image must be binary in order to connect pixels
		Picture copy = new Picture(binaryComponentImage());
		
		
		// Pixels located at coordinates (x,y) are mapped to
		// an index in the array in WeightedQuickUnion according
		// to the following equation:
		// i = ( (y * width) + x)

		// First Scan
		// Scan through image, give each pixel a label based on colour
		// Assume top left most pixel, (0, 0) is part of the background

		// StdOut.println("First Pass: Assigning Labels");
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {

				// i = index of current pixel
				int i = (y * w + x);

				// Assume the first pixel is background
				// If pixel is same colour as background
				// don't analyse pixel
				// just union it with the first pixel
				if ((copy.get(x, y).equals(copy.get(0, 0))) &&
				// Do not union first pixel with first pixel
				// This will result in an inaccurate component count
						((x > 0) || (y > 0))) {
					uf.union(i, 0);
				} else {

					// Check pixel not in topmost row or leftmost column
					// If pixel to left and top both have same value,
					// union with whichever has lower root
					// union with either if they have the same root
					if ((x > 0) && (copy.get(x, y).equals(copy.get(x - 1, y)))
							&& (y > 0)
							&& (copy.get(x, y).equals(copy.get(x, y - 1)))) {
						// assign lower root to current pixel
						// if root(above) < root(left)
						if (uf.root((y - 1) * w + x) <= uf
								.root((y * w) + x - 1)) {
							uf.union(i, ((y - 1) * w + x));
						} else {
							uf.union(i, (y * w + x - 1));
						}
						// StdOut.println("(" + x + ", " + y +
						// "): Pixel to left and above have same value.");

						// Check not in leftcolumn row
						// if pixel to left has same value, union with current
						// pixel
					} else if ((x > 0)
							&& (copy.get(x, y).equals(copy.get(x - 1, y)))) {
						uf.union(i, (y * w + x - 1));
						// StdOut.println("(" + x + ", " + y +
						// "): Pixel left has same value");

						// Check not in topmost row
						// if pixel above has same value,, union with current
						// pixel
					} else if ((y > 0)
							&& (copy.get(x, y).equals(copy.get(x, y - 1)))) {
						uf.union(i, (y - 1) * w + x);
						// StdOut.println("(" + x + ", " + y +
						// "): Pixel above has same value");
					}

					// Implied else statement here
					// If neither pixel above or left has same value
					// then do nothing, pixel is not connected
					// so it is remains with its intialised (and thus unique)
					// root

					// StdOut.println("(" + x + ", " + y + ") ID: " +
					// uf.getId(i) +
					// ", Root: " + uf.root(i));
				}
			}
		}

		// Now scan to detect equivalent roots
		// This means scanning through pixels for adjacent same colour pixels
		// with different roots
		// this means they are part of the same component but have not yet been
		// connected

		// StdOut.println("Second Pass: Detecting Equivalent Roots");
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {

				int i = (y * w + x);

				if (uf.root(i) != 0) {
					// check left for same value, but different root
					if ((x > 0) && (copy.get(x, y).equals(copy.get(x - 1, y)))
							&& (uf.root(i) != uf.root(y * w + x - 1))) {
						uf.union(i, (y * w + x - 1));
					}
					// check above for same value, but different root
					if ((y > 0) && (copy.get(x, y).equals(copy.get(x, y - 1)))
							&& (uf.root(i) != uf.root((y - 1) * w + x))) {
						uf.union(i, ((y - 1) * w + x));
					}
				}

				// StdOut.println("(" + x + ", " + y + ") ID: " + uf.getId(i) +
				// ", Root: " + uf.root(i));
			}
		}

		// //Not necessary
		// For console testing purposes only
		// StdOut.println("Third Pass: Root Check");
		// for (int y = 0; y < h; y++) {
		// for (int x = 0; x < w; x++) {
		//
		// int i = (y * w + x);
		// StdOut.println("(" + x + ", " + y + ") ID: " + uf.getId(i) +
		// ", Root: " + uf.root(i));
		//
		// }
		// }
		connected = true;
	}
	
	/**
	 * Builds a map of unique roots, each one corresponding to an object in the
	 * image Maps each root to a randomly generated colour Can be used to return
	 * a set of unique roots
	 */
	private void buildRootsSet() {
		
		// Use union find algorithms to connect pixels
		// into object before continuing
		if (!connected) {
			connectComponentsOnePass();
		}
		
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				int i = uf.root(y * w + x);
				// Zero will be a root in the image corresponding
				// to the background
				// Do not include this as the background does not
				// count as an object
				if (i > 0) {
					roots.put(uf.root(y * w + x), getRandomColour());
				}
			}
		}
		rootsSetBuilt = true;
	}
	
	/**
	 * Generates a random colour
	 * 
	 * @return a Color object of random value
	 */
	private Color getRandomColour() {

		int r = (int) (255 * Math.random());
		int g = (int) (255 * Math.random());
		int b = (int) (255 * Math.random());
		return new Color(r, g, b);
	}

	/**
	 * Draws a bounding box around a component
	 * 
	 * @param lowerY
	 *            value of y for the line defining the lower horizontal line
	 * @param lowerX
	 *            value of x for the line defining the lower vertical line
	 * @param upperY
	 *            value of y for the line defining the higher horizontal line
	 * @param upperX
	 *            value of x for the line defining the higher vertical line
	 */
	private Picture drawBoundary(Picture pic, int lowerY, int lowerX,
			int upperY, int upperX) {

		for (int x = lowerX; x <= upperX; x++) {
			//check for values out of bounds to avoid
			//trying to draw lines outside image
			//when objects are on edge of image, 
			if ( (x >= 0) && (x  < w) ) {
				if (lowerY >= 0) pic.set(x, lowerY, Color.RED);
				if (upperY < h) pic.set(x, upperY, Color.RED);
			}
		}
		// No need to set first and last pixel this time
		// they are the corner points
		// Thus, they are already set to red above
		for (int y = lowerY + 1; y < upperY; y++) {
			if (lowerX >= 0) pic.set(lowerX, y, Color.RED);
			if (upperX < w) pic.set(upperX, y, Color.RED);
		}
		return pic;
	}

	/**
	 * Method to initialise fields
	 * 
	 * Changing the threshold may change the number of
	 * objects in the image when binarised
	 * Therefore calling this method clear old vaues from fields
	 */
	private void initialise() {
		roots = new HashMap<Integer, Color>();
		uf = new WeightedQuickUnionUF(pic.width() * pic.height());
		w = pic.width();
		h = pic.height();
		connected = false;
		rootsSetBuilt = false;
	}
	
	private void connectComponentsOnePass() {
		
		// Image must binary in order to connect pixels
		Picture copy = new Picture(binaryComponentImage());
		
		
		// Pixels located at coordinates (x,y) are mapped to
		// an index in the array in WeightedQuickUnion according
		// to the following equation:
		// i = ( (y * width) + x)

		// First Scan
		// Scan through image, give each pixel a label based on colour
		// Assume top left most pixel, (0, 0) is part of the background

		// StdOut.println("First Pass: Assigning Labels");
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {

				// i = index of current pixel
				int i = (y * w + x);

				// Assume the first pixel is background
				// If pixel is same colour as background
				// don't analyse pixel
				// just union it with the first pixel
				if ((copy.get(x, y).equals(copy.get(0, 0))) &&
						
						// Do not union first pixel with first pixel
						// This will result in an inaccurate component count
						((x > 0) || (y > 0))) {
					uf.union(i, 0);
				} else {

					// Check pixel not in topmost row or leftmost column
					// If pixel to left and top both have same value,
					
					if ((x > 0) && (copy.get(x, y).equals(copy.get(x - 1, y)))
							&& (y > 0)
							&& (copy.get(x, y).equals(copy.get(x, y - 1)))) {
						
						// If above and left have same root, union with either
						// If they have different root,
						// union with whichever has lower root,
						// Then union with the other
						if (uf.root((y - 1) * w + x) <= uf
								.root((y * w) + x - 1)) {
							uf.union(i, ((y - 1) * w + x));
							if (uf.root((y - 1) * w + x) != uf
									.root((y * w) + x - 1)) {
								uf.union(i, (y * w) + x - 1);
							}
						} else {
							uf.union(i, (y * w + x - 1));
							if (uf.root((y * w) + x - 1) != 
									(uf.root((y - 1) * w + x) ) ) {
								uf.union(i, ((y - 1) * w + x));
							}
						}

					// Check not in leftmost column
					// if pixel to left has same value, union with current
					// pixel
					} else if ((x > 0)
							&& (copy.get(x, y).equals(copy.get(x - 1, y)))) {
						uf.union(i, (y * w + x - 1));


					// Check not in topmost row
					// if pixel above has same value,, union with current
					// pixel
					} else if ((y > 0)
							&& (copy.get(x, y).equals(copy.get(x, y - 1)))) {
						uf.union(i, (y - 1) * w + x);
					}

					// Implied else statement here
					// If neither pixel above or left has same value
					// then do nothing, pixel is not connected
					// so it is remains with its intialised (and thus unique)
					// root

					// StdOut.println("(" + x + ", " + y + ") ID: " +
					// uf.getId(i) +
					// ", Root: " + uf.root(i));
				}
			}
		}
		// //Not necessary
		// For console testing purposes only
		// StdOut.println("Third Pass: Root Check");
		// for (int y = 0; y < h; y++) {
		// for (int x = 0; x < w; x++) {
		//
		// int i = (y * w + x);
		// StdOut.println("(" + x + ", " + y + ") ID: " + uf.getId(i) +
		// ", Root: " + uf.root(i));
		//
		// }
		// }
		StdOut.println("No second pass needed");
		connected = true;
	}
	
}
