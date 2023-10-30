package com.jeffdisher.cacophony.exporter;

import com.jeffdisher.cacophony.exporter.modes.RawTemplate;
import com.jeffdisher.cacophony.utils.Pair;


/**
 * Command-line option constants and some parsing helpers.
 */
public class CommandParser
{
	/**
	 * Puts the exporter into the "user" mode, walking this user's description and records.
	 * The argument is the user's public key.
	 * NOTE:  This is currently the only "mode", but a single post mode may be added later so this wording is used.
	 */
	public static final String MODE_USER = "--user";

	/**
	 * Followed by:
	 * -user template location
	 * -inline template for generated file name
	 */
	public static final String TRI_SAVE_USERS = "--saveUsers";
	/**
	 * Followed by:
	 * -record template location
	 * -inline template for generated file name
	 * Specifying this will cause the records and feature for the given user to be walked.
	 */
	public static final String TRI_SAVE_RECORDS = "--saveRecords";

	/**
	 * Specifies a file where built-in macros are specified for path templates.  The contents of this file will be
	 * prepended to all path templates but not content templates (which also means that you need to be careful with
	 * whitespace as this can break paths).
	 */
	public static final String KEY_MACRO_FILE = "--pathMacroFile";
	/**
	 * Specified if user pics should be written.  The argument is an inline template of the file name.
	 */
	public static final String KEY_SAVE_USER_PIC = "--saveUserPic";
	/**
	 * Specified if thumbnails should be written.  The argument is an inline template of the file name.
	 */
	public static final String KEY_SAVE_THUMBNAIL = "--saveThumbnail";
	/**
	 * Specified if audio clips should be written.  The argument is an inline template of the file name.
	 */
	public static final String KEY_SAVE_AUDIO = "--saveAudio";
	/**
	 * Specified if video clips should be written.  The argument is an inline template of the file name.
	 */
	public static final String KEY_SAVE_VIDEO = "--saveVideo";

	/**
	 * Specified in "user" mode to instruct the exporter to walk any replyTo links in records which were walked
	 * (including other replyTo records).
	 */
	public static final String FLAG_INCLUDE_REPLY_TO = "--includeReplyTo";
	/**
	 * Specified in "user" mode to instruct the exporter to invoke the "userTemplate" on any user's referenced via
	 * "replyTo" links.
	 * NOTE:  These users will NOT have their records walked.
	 */
	public static final String FLAG_INCLUDE_REFERENCED_USERS = "--includeReferencedUsers";


	public static Pair<String, RawTemplate> getTri(String[] args, String key, String builtInMacros)
	{
		Pair<String, RawTemplate> value = null;
		for (int i = 0; i < (args.length - 2); ++i)
		{
			String arg = args[i];
			if (key.equals(arg))
			{
				value = new Pair<>(args[i+1], RawTemplate.filePathTemplate(args[i+2], builtInMacros));
			}
		}
		return value;
	}

	public static String getValue(String[] args, String key)
	{
		String value = null;
		for (int i = 0; i < (args.length - 1); ++i)
		{
			String arg = args[i];
			if (key.equals(arg))
			{
				value = args[i+1];
			}
		}
		return value;
	}

	public static boolean checkFlag(String[] args, String flag)
	{
		boolean match = false;
		for (int i = 0; i < args.length; ++i)
		{
			String arg = args[i];
			if (flag.equals(arg))
			{
				match = true;
				break;
			}
		}
		return match;
	}
}
