package com.jeffdisher.cacophony.exporter;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;

import com.jeffdisher.cacophony.exporter.models.SoftRecordMapModel;
import com.jeffdisher.cacophony.exporter.models.LazyUserDescriptionMapModel;
import com.jeffdisher.cacophony.exporter.modes.Environment;
import com.jeffdisher.cacophony.exporter.modes.RawTemplate;
import com.jeffdisher.cacophony.exporter.modes.UserMode;
import com.jeffdisher.cacophony.net.IpfsConnection;
import com.jeffdisher.cacophony.types.IpfsKey;
import com.jeffdisher.cacophony.utils.Pair;

import io.ipfs.api.IPFS;
import io.ipfs.multiaddr.MultiAddress;


public class Exporter
{
	public static void main( String[] args ) throws Throwable
	{
		// Parse the relevant command line options.
		// We want to read the macros first since we want to provide those to the other templates.
		String rawPublicKey = (args.length > 0) ? CommandParser.getValue(args, CommandParser.MODE_USER) : null;
		IpfsKey publicKey = (null != rawPublicKey) ? IpfsKey.fromPublicKey(rawPublicKey) : null;
		if (null != publicKey)
		{
			String macroFile = CommandParser.getValue(args, CommandParser.KEY_MACRO_FILE);
			String macroTemplateText = (null != macroFile) ? Files.readString(new File(macroFile).toPath()) : "";
			Pair<String, RawTemplate> saveUsers = CommandParser.getTri(args, CommandParser.TRI_SAVE_USERS, macroTemplateText);
			String rawUserPicPathTemplate = CommandParser.getValue(args, CommandParser.KEY_SAVE_USER_PIC);
			Pair<String, RawTemplate> saveUserRecords = CommandParser.getTri(args, CommandParser.TRI_SAVE_RECORDS, macroTemplateText);
			String rawThumbnailPathTemplate = CommandParser.getValue(args, CommandParser.KEY_SAVE_THUMBNAIL);
			String rawAudioPathTemplate = CommandParser.getValue(args, CommandParser.KEY_SAVE_AUDIO);
			String rawVideoPathTemplate = CommandParser.getValue(args, CommandParser.KEY_SAVE_VIDEO);
			boolean includeReplyTo = CommandParser.checkFlag(args, CommandParser.FLAG_INCLUDE_REPLY_TO);
			boolean includeReferencedUsers = CommandParser.checkFlag(args, CommandParser.FLAG_INCLUDE_REFERENCED_USERS);
			
			_runUserMode(publicKey
					, macroTemplateText
					, saveUsers
					, rawUserPicPathTemplate
					, saveUserRecords
					, rawThumbnailPathTemplate
					, rawAudioPathTemplate
					, rawVideoPathTemplate
					, includeReplyTo
					, includeReferencedUsers
			);
		}
		else
		{
			_printUsageString(System.err);
		}
	}


	private static void _runUserMode(IpfsKey publicKey
			, String macroTemplateText
			, Pair<String, RawTemplate> saveUsers
			, String rawUserPicPathTemplate
			, Pair<String, RawTemplate> saveUserRecords
			, String rawThumbnailPathTemplate
			, String rawAudioPathTemplate
			, String rawVideoPathTemplate
			, boolean includeReplyTo
			, boolean includeReferencedUsers
	) throws IOException, Throwable
	{
		// Create the environment.
		IPFS defaultConnection = new IPFS(new MultiAddress("/ip4/127.0.0.1/tcp/5001"));
		IpfsConnection connection = new IpfsConnection(null, defaultConnection, defaultConnection);
		RawTemplate userTemplateString = null;
		RawTemplate userPathTemplate = null;
		if (null != saveUsers)
		{
			File templateFile = new File(saveUsers.first());
			userTemplateString = RawTemplate.fileContentTemplate(Files.readString(templateFile.toPath()), templateFile.getParentFile());
			userPathTemplate = saveUsers.second();
		}
		RawTemplate userPicPathTemplate = (null != rawUserPicPathTemplate)
				? RawTemplate.filePathTemplate(rawUserPicPathTemplate, macroTemplateText)
				: null
		;
		RawTemplate recordTemplateString = null;
		RawTemplate recordPathTemplate = null;
		if (null != saveUserRecords)
		{
			File templateFile = new File(saveUserRecords.first());
			recordTemplateString = RawTemplate.fileContentTemplate(Files.readString(templateFile.toPath()), templateFile.getParentFile());
			recordPathTemplate = saveUserRecords.second();
		}
		RawTemplate thumbnailPathTemplate = (null != rawThumbnailPathTemplate)
				? RawTemplate.filePathTemplate(rawThumbnailPathTemplate, macroTemplateText)
				: null
		;
		RawTemplate audioPathTemplate = (null != rawAudioPathTemplate)
				? RawTemplate.filePathTemplate(rawAudioPathTemplate, macroTemplateText)
				: null
		;
		RawTemplate videoPathTemplate = (null != rawVideoPathTemplate)
				? RawTemplate.filePathTemplate(rawVideoPathTemplate, macroTemplateText)
				: null
		;
		
		SoftRecordMapModel recordMap = new SoftRecordMapModel(connection);
		LazyUserDescriptionMapModel userMap = new LazyUserDescriptionMapModel(connection);
		Environment env = new Environment(connection
				, recordMap
				, userMap
				, includeReplyTo
				, includeReferencedUsers
				
				, userTemplateString
				, userPathTemplate
				, userPicPathTemplate
				
				, recordTemplateString
				, recordPathTemplate
				, thumbnailPathTemplate
				, audioPathTemplate
				, videoPathTemplate
		);
		UserMode.runUserMode(env, publicKey);
	}

	private static void _printUsageString(PrintStream stream)
	{
		stream.println("Usage:  Exporter --user public_key"
				+ " [--pathMacroFile path]"
				+ " [--saveUsers path templated_path]"
				+ " [--saveRecords path templated_path]"
				+ " [--saveUserPic template_path]"
				+ " [--saveThumbnail template_path]"
				+ " [--saveAudio template_path]"
				+ " [--saveVideo template_path]"
				+ " [--includeReplyTo]"
				+ " [--includeReferencedUsers]"
		);
		stream.println("The \"--user\" option is the only required one and must be given a public key in the usually Cacophony style (starts with \"z\").");
		stream.println("The other options which take \"template_path\" are to allow per-user or per-record paths to change.  This means that they are templates, themselves.");
		stream.println("A file specified with \"--pathMacroFile\" with be treated as a raw prefix path templates (but not content templates).  This means it shouldn't include newlines (as they could break the paths).");
	}
}
