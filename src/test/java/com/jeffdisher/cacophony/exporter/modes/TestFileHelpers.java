package com.jeffdisher.cacophony.exporter.modes;

import org.junit.Assert;
import org.junit.Test;


public class TestFileHelpers
{
	@Test
	public void relativePaths()
	{
		String one = "./foo/bar.dat";
		String two = "./bar/nested/vol.dat";
		String accessTwo_fromOne = FileHelpers.relativePath(one, two);
		String accessOne_fromTwo = FileHelpers.relativePath(two, one);
		Assert.assertEquals("../bar/nested/vol.dat", accessTwo_fromOne);
		Assert.assertEquals("../../foo/bar.dat", accessOne_fromTwo);
	}
}
