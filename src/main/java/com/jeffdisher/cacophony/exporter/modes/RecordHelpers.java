package com.jeffdisher.cacophony.exporter.modes;

import java.util.List;

import com.jeffdisher.cacophony.data.global.AbstractRecord;


public class RecordHelpers
{
	public static AbstractRecord.Leaf findAudio(AbstractRecord record)
	{
		return _findMatchingLeaf(record, "audio/");
	}

	public static AbstractRecord.Leaf findVideo(AbstractRecord record)
	{
		return _findMatchingLeaf(record, "video/");
	}


	private static AbstractRecord.Leaf _findMatchingLeaf(AbstractRecord record, String mimePrefix)
	{
		AbstractRecord.Leaf match = null;
		List<AbstractRecord.Leaf> leaves = record.getVideoExtension();
		if (null != leaves)
		{
			for (AbstractRecord.Leaf leaf : leaves)
			{
				String mime = leaf.mime();
				if (mime.startsWith(mimePrefix))
				{
					match = leaf;
					// Just pick the first match.
					break;
				}
			}
		}
		return match;
	}
}
