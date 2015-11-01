package imageprocessing;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.princeton.cs.introcs.Picture;

public class ComponentImageTest {

	private Picture pic;
	private ComponentImage ci;

	@Before
	public void setUp() throws Exception {
		pic = new Picture("images/test-four-objects.bmp");
		ci = new ComponentImage(pic, 128);
	}

	@After
	public void tearDown() throws Exception {
		pic = null;
		ci = null;
	}

	@Test
	public void testGetPicture() {

		// Test picture exists in instance of Component Image
		// Type of test: Exist
		assertNotNull(ci.getPicture());

		// Test picture is the picture used when constructing instance of
		// ComponentImage
		// Type of test: Right
		assertEquals(pic, ci.getPicture());

		// Test creating instance if component image
		// with file that is not a invalid file type
		// Type of test: Force Error Conditions
		try {
			Picture img = new Picture("images/test-four-objects.txt");
			ci = new ComponentImage(img, 128);
			fail("Should throw exception");
		} catch (RuntimeException e) {
			assertTrue(true);
		}
	}

	@Test
	public void testBinaryComponentImage() {
		// Test that BinaryComponentImage returns an image i.e. a Picture object
		// Type of test: Conformance
		assertSame(pic.getClass(), Picture.class);

		// Test that image returned is not the original image
		// Type of test: Right
		assertNotSame(pic, ci.binaryComponentImage());
	}

	@Test
	public void testBinarisedImages() {
		// Test that BinaryComponentImage produces all black
		// and all white images for threshold limits
		// Type of test: Cross-check
		Picture black = new Picture("images/test-no-objects-black.bmp");
		Picture white = new Picture("images/test-no-objects-white.bmp");

		assertNotEquals(black, ci.binaryComponentImage());
		assertNotEquals(white, ci.binaryComponentImage());

		ci.setThreshold(256);
		assertEquals(black, ci.binaryComponentImage());

		ci.setThreshold(0);
		assertEquals(white, ci.binaryComponentImage());

		// Test that BinaryComponentImage returns
		// different images when using different thresholds;
		// Type of test: Cross-check
		ci.setThreshold(90);
		Picture lowThreshold = ci.binaryComponentImage();
		ci.setThreshold(130);
		Picture highThreshold = ci.binaryComponentImage();
		assertNotEquals(lowThreshold, highThreshold);
	}

	@Test
	public void testCountComponents() {

		// Test that countComponents correctly identifies
		// the number of object in an image
		// Type of test: Cardinality

		ci = new ComponentImage(
				new Picture("images/test-no-objects-white.bmp"), 128);
		assertEquals(0, ci.countComponents());

		ci = new ComponentImage(
				new Picture("images/test-no-objects-black.bmp"), 128);
		assertEquals(0, ci.countComponents());

		ci = new ComponentImage(new Picture("images/test-one-object.bmp"), 128);
		assertEquals(1, ci.countComponents());

		ci = new ComponentImage(new Picture("images/test-four-objects.bmp"),
				150);
		assertEquals(4, ci.countComponents());
	}

	@Test
	public void testCountWithVaryingThreshold() {

		// Test that countComponents correctly identifies
		// a different number of objects
		// in an image with objects of various colours
		// by using a different Threshold

		ci = new ComponentImage(new Picture("images/test-four-objects.bmp"), 80);
		assertEquals(0, ci.countComponents());
		ci.setThreshold(90);
		assertEquals(1, ci.countComponents());
		ci.setThreshold(100);
		assertEquals(2, ci.countComponents());
		ci.setThreshold(130);
		assertEquals(3, ci.countComponents());
		ci.setThreshold(150);
		assertEquals(4, ci.countComponents());
	}

	@Test
	public void testColourComponentImage() {

		// Test that ColourComponentImage returns an image i.e. a Picture object
		// Type of test: Conformance
		assertSame(pic.getClass(), Picture.class);

		// Test that image returned is not the original image
		// Type of test: Right
		assertNotSame(pic, ci.colourComponentImage());

		// Test that ColourComponentImage returns
		// different images when using different thresholds;
		// Type of test: Cross-check
		ci.setThreshold(90);
		Picture lowThreshold = ci.colourComponentImage();
		ci.setThreshold(130);
		Picture highThreshold = ci.colourComponentImage();
		assertNotEquals(lowThreshold, highThreshold);
	}

	@Test
	public void testHighlightComponentImage() {

		// Test that HighlightComponentImage returns an image i.e. a Picture
		// object
		// Type of test: Conformance
		assertSame(pic.getClass(), Picture.class);

		// Test that image returned is not the original image
		// Type of test: Right
		assertNotSame(pic, ci.highlightComponentImage(pic));

		// Test that BinaryComponentImage returns
		// different images when using different thresholds;
		// Type of test: Cross-check
		ci.setThreshold(90);
		Picture lowThreshold = ci.highlightComponentImage(pic);
		ci.setThreshold(130);
		Picture highThreshold = ci.highlightComponentImage(pic);
		assertNotEquals(lowThreshold, highThreshold);
	}

	@Test
	public void testGetThreshold() {

		// Test getting threshold at initial value
		// Type of test: Right
		assertEquals(128, ci.getThreshold());
	}

	@Test
	public void testInitialiseThreshold() {

		// Test intialising the threshold in Component Image contructor

		// Type of test: Range
		ci = new ComponentImage(pic, -1);
		assertEquals(0, ci.getThreshold());

		// Type of test: Range
		ci = new ComponentImage(pic, 0);
		assertEquals(0, ci.getThreshold());

		// Type of test: Right
		ci = new ComponentImage(pic, 150);
		assertEquals(150, ci.getThreshold());

		// Type of test: Range
		ci = new ComponentImage(pic, 256);
		assertEquals(256, ci.getThreshold());

		// Type of test: Range
		ci = new ComponentImage(pic, 257);
		assertEquals(256, ci.getThreshold());
	}

	@Test
	public void testSetThreshold() {

		// Test setting threshold
		// Type of test: Right
		ci.setThreshold(1);
		assertEquals(1, ci.getThreshold());
		ci.setThreshold(100);
		assertEquals(100, ci.getThreshold());
		ci.setThreshold(200);
		assertEquals(200, ci.getThreshold());
		ci.setThreshold(254);
		assertEquals(254, ci.getThreshold());

		// Test setting threshold at boundary limits
		// Type of test: Range
		ci.setThreshold(0);
		assertEquals(0, ci.getThreshold());
		ci.setThreshold(256);
		assertEquals(256, ci.getThreshold());

		// Test setting threshold out of valid range
		// Type of test: Range
		ci.setThreshold(-1);
		assertEquals(0, ci.getThreshold());
		ci.setThreshold(257);
		assertEquals(256, ci.getThreshold());
	}

}
