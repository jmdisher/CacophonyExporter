package com.jeffdisher.cacophony.exporter.modes;

import java.util.HashMap;
import java.util.Map;

import com.jeffdisher.cacophony.data.global.AbstractRecord;
import com.jeffdisher.cacophony.exporter.models.RecordModel;
import com.jeffdisher.cacophony.types.IpfsFile;
import com.jeffdisher.cacophony.types.IpfsKey;


/**
 * The record mode template context includes these top-level elements:
 * -cid (the CID of the record being walked)
 * -record (the record data)
 * -recordMap (a lazily-loaded map which includes record data resolved by CID)
 * -userMap (a lazily-loaded map which includes user data resolved by key)
 * -relativePaths (not visible for path processing, only the main file template, and are relative to that main file output)
 *  -thumbnail
 *  -audio
 *  -video
 */
public class RecordMode
{
	public static final String TOP_CID = "cid";
	public static final String TOP_RECORD = "record";
	public static final String TOP_RECORD_MAP = "recordMap";
	public static final String TOP_USER_MAP = "userMap";
	public static final String TOP_RELATIVE_PATHS = "relativePaths";
	public static final String PATHS_THUMBNAIL = "thumbnail";
	public static final String PATHS_AUDIO = "audio";
	public static final String PATHS_VIDEO = "video";

	public static void runRecordMode(Environment env, IpfsFile cid, IpfsKey topLevelUser) throws Throwable
	{
		AbstractRecord record = AbstractRecord.DESERIALIZER.apply(env.cacophonyConnection().loadData(cid));
		Map<String, Object> modeMap = new HashMap<>();
		modeMap.put(TOP_CID, cid.toSafeString());
		modeMap.put(TOP_RECORD, new RecordModel(record));
		modeMap.put(TOP_RECORD_MAP, env.recordMap());
		modeMap.put(TOP_USER_MAP, env.userMap());
		
		// Get the paths and add them to the map before processing the main file.
		String recordPath = (null != env.path_recordPathTemplate())
				? env.path_recordPathTemplate().processWithContext("record path", modeMap)
				: null
		;
		String thumbnailPath = null;
		String audioPath = null;
		String videoPath = null;
		if ((null != env.path_thumbnailPathTemplate()) && (null != record.getThumbnailCid()))
		{
			thumbnailPath = env.path_thumbnailPathTemplate().processWithContext("record thumbnail", modeMap);
			FileHelpers.saveNetworkToFile(thumbnailPath, env.cacophonyConnection(), record.getThumbnailCid());
		}
		if (null != env.path_audioPathTemplate())
		{
			AbstractRecord.Leaf leaf = RecordHelpers.findAudio(record);
			if (null != leaf)
			{
				audioPath = env.path_audioPathTemplate().processWithContext("record audio", modeMap);
				FileHelpers.saveNetworkToFile(audioPath, env.cacophonyConnection(), leaf.cid());
			}
		}
		if (null != env.path_videoPathTemplate())
		{
			AbstractRecord.Leaf leaf = RecordHelpers.findVideo(record);
			if (null != leaf)
			{
				videoPath = env.path_videoPathTemplate().processWithContext("record video", modeMap);
				FileHelpers.saveNetworkToFile(videoPath, env.cacophonyConnection(), leaf.cid());
			}
		}
		
		if (null != recordPath)
		{
			modeMap.put(TOP_RELATIVE_PATHS, _buildRelativePaths(recordPath, thumbnailPath, audioPath, videoPath));
			String output = env.content_recordTemplateString().processWithContext("record content", modeMap);
			FileHelpers.saveStringToFile(recordPath, output);
		}
		
		if ((null != record.getReplyTo()) && env.includeReplyTo())
		{
			// This is not ideal, but we will just use recursion for now, since it is obvious.
			RecordMode.runRecordMode(env, record.getReplyTo(), topLevelUser);
		}
		// If this isn't posted by the user we are walking, and we want to include referenced users, generate description-only for them.
		// Typically, we don't include referenced users if we want to fully generate them, too.
		if (!record.getPublisherKey().equals(topLevelUser) && env.includeReferencedUsers())
		{
			UserMode.writeDescriptionOnly(env, record.getPublisherKey());
		}
	}


	private static Map<String, String> _buildRelativePaths(String recordPath, String thumbnailPath, String audioPath, String videoPath)
	{
		String relativeThumbnail = null;
		String relativeAudio = null;
		String relativeVideo = null;
		if (null != recordPath)
		{
			relativeThumbnail = (null != thumbnailPath)
					? FileHelpers.relativePath(recordPath, thumbnailPath)
					: null
			;
			relativeAudio = (null != audioPath)
					? FileHelpers.relativePath(recordPath, audioPath)
					: null
			;
			relativeVideo = (null != videoPath)
					? FileHelpers.relativePath(recordPath, videoPath)
					: null
			;
		}
		Map<String, String> map = new HashMap<>();
		if (null != relativeThumbnail)
		{
			map.put(PATHS_THUMBNAIL, relativeThumbnail);
		}
		if (null != relativeAudio)
		{
			map.put(PATHS_AUDIO, relativeAudio);
		}
		if (null != relativeVideo)
		{
			map.put(PATHS_VIDEO, relativeVideo);
		}
		return map;
	}
}
