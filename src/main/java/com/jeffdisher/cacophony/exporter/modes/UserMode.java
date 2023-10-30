package com.jeffdisher.cacophony.exporter.modes;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jeffdisher.cacophony.data.global.AbstractDescription;
import com.jeffdisher.cacophony.data.global.AbstractRecords;
import com.jeffdisher.cacophony.exporter.models.UserDescriptionModel;
import com.jeffdisher.cacophony.types.IpfsConnectionException;
import com.jeffdisher.cacophony.types.IpfsFile;
import com.jeffdisher.cacophony.types.IpfsKey;

import freemarker.template.TemplateException;


/**
 * The user mode template context includes these top-level elements:
 * -publicKey (that public key of the user)
 * -user (the user description)
 * -recordCids (the list of CIDs of this user's records - oldest first)
 * -recordMap (a lazily-loaded map which includes record data resolved by CID)
 * -userPicPath (not visible for path processing, only the main file template)
 * -shouldShowThumbnails (true if record thumbnails are being stored in this run)
 */
public class UserMode
{
	public static final String TOP_PUBLIC_KEY = "publicKey";
	public static final String TOP_USER = "user";
	public static final String TOP_RECORD_CIDS = "recordCids";
	public static final String TOP_RECORD_MAP = "recordMap";
	public static final String TOP_USER_PIC_PATH = "userPicPath";
	public static final String TOP_SHOULD_SHOW_THUMBNAILS = "shouldShowThumbnails";

	public static void runUserMode(Environment env, IpfsKey publicKey) throws Throwable
	{
		boolean shouldShowThumbnails = (null != env.path_thumbnailPathTemplate());
		AbstractRecords records = _writeDescription(env, publicKey, true, shouldShowThumbnails);
		
		// We want to walk the records if any per-record data was requested.
		boolean shouldWalkRecords = (null != env.content_recordTemplateString())
				|| (null != env.path_recordPathTemplate())
				|| (null != env.path_thumbnailPathTemplate())
				|| (null != env.path_audioPathTemplate())
				|| (null != env.path_videoPathTemplate())
		;
		if (shouldWalkRecords)
		{
			for (IpfsFile record : records.getRecordList())
			{
				RecordMode.runRecordMode(env, record, publicKey);
			}
		}
	}

	public static void writeDescriptionOnly(Environment env, IpfsKey publicKey)
	{
		try
		{
			_writeDescription(env, publicKey, false, false);
		}
		catch (IpfsConnectionException | IOException | TemplateException e)
		{
			// In this case, we ignore the error since this path is used for best-efforts cases which can fail.
			System.out.println("WARNING:  Failed to write description for non-critical user: " + publicKey.toPublicKey());
			e.printStackTrace();
		}
	}


	private static AbstractRecords _writeDescription(Environment env, IpfsKey publicKey, boolean shouldAddRecords, boolean shouldShowThumbnails) throws IOException, TemplateException, IpfsConnectionException
	{
		AbstractRecords records = null;
		AbstractDescription description = env.userMap().getDescription(publicKey);
		if (null != description)
		{
			records = env.userMap().getRecords(publicKey);
			List<String> recordCids = shouldAddRecords
					? records.getRecordList()
						.stream().map((IpfsFile cid) -> cid.toSafeString())
						.toList()
					: Collections.emptyList()
			;
			Map<String, Object> modeMap = new HashMap<>();
			modeMap.put(TOP_PUBLIC_KEY, publicKey.toPublicKey());
			modeMap.put(TOP_USER, new UserDescriptionModel(description));
			modeMap.put(TOP_RECORD_CIDS, recordCids);
			modeMap.put(TOP_RECORD_MAP, env.recordMap());
			modeMap.put(TOP_SHOULD_SHOW_THUMBNAILS, shouldShowThumbnails);
			
			String userPath = (null != env.path_userPathTemplate())
					? env.path_userPathTemplate().processWithContext("user path", modeMap)
					: null
			;
			String userPicPath = (null != env.path_userPicPathTemplate() && (null != description.getPicCid()))
					? env.path_userPicPathTemplate().processWithContext("user pic", modeMap)
					: null
			;
			modeMap.put(TOP_USER_PIC_PATH, ((null != userPath) && (null != userPicPath))
					? FileHelpers.relativePath(userPath, userPicPath)
					: null
			);
			
			if (null != userPath)
			{
				String output = env.content_userTemplateString().processWithContext("user content", modeMap);
				FileHelpers.saveStringToFile(userPath, output);
			}
			if (null != userPicPath)
			{
				FileHelpers.saveNetworkToFile(userPicPath, env.cacophonyConnection(), description.getPicCid());
			}
		}
		return records;
	}
}
