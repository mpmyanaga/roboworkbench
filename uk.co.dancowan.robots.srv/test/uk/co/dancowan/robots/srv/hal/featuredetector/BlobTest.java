/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *  Copyright (C) 2009  Dan Cowan
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details (www.gnu.org/licenses)
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package uk.co.dancowan.robots.srv.hal.featuredetector;

import java.util.List;

import junit.framework.TestCase;

/**
 * Tests for parsing blobs from expected result string.
 * 
 * @author Dan Cowan
 * @since version 1.0.0
 */
public class BlobTest extends TestCase
{
	/**
	 * Test method for {@link uk.co.dancowan.robots.srv.hal.featuredetector.FeatureDetector#processBlobData(java.lang.String)}.
	 */
	public void testProcessMessage()
	{
		FeatureDetector fd = new FeatureDetector();
		char nl = (char) 13; // \r newline character

		List<Blob> result = fd.processBlobData("##vb0 0" + nl);
		assertEquals(0, result.size());
		
		result = fd.processBlobData("##vb0 0" + nl + " 100 - 10");
		assertEquals(0, result.size());
		
		result = fd.processBlobData("##vb0 0" + nl + " 100 - 10 10 10 10" + nl + " 200 - 20 20 20 20" + nl);
		assertEquals(2, result.size());
		
		result = fd.processBlobData("##vb0 0" + nl + " 100 - 10 10 10 10" + nl + " 200 - 20 20 20 20" + nl + " 50 - 30 30 30 30" + nl + " 1000 - 40 40 40 40" + nl);
		assertEquals(4, result.size());
	}
}
