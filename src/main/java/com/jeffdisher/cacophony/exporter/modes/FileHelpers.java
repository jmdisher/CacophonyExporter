package com.jeffdisher.cacophony.exporter.modes;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import com.jeffdisher.cacophony.types.IConnection;
import com.jeffdisher.cacophony.types.IpfsConnectionException;
import com.jeffdisher.cacophony.types.IpfsFile;


public class FileHelpers
{
	public static void saveStringToFile(String filePath, String data) throws IOException
	{
		File file = new File(filePath);
		if (!file.exists())
		{
			// We ignore this result since they usually already exist.
			file.getParentFile().mkdirs();
			Files.writeString(file.toPath(), data, StandardOpenOption.CREATE_NEW);
			System.out.println("Wrote string file: " + file.getAbsolutePath());
		}
	}

	public static void saveNetworkToFile(String filePath, IConnection connection, IpfsFile cid) throws IOException, IpfsConnectionException
	{
		File file = new File(filePath);
		if (!file.exists())
		{
			// The file could be large so we will use the streaming approach.
			try (InputStream stream = connection.loadDataAsStream(cid))
			{
				// We ignore this result since they usually already exist.
				file.getParentFile().mkdirs();
				Files.copy(stream, file.toPath());
				System.out.println("Wrote binary file: " + file.getAbsolutePath());
			}
		}
	}

	public static String relativePath(String sourceFile, String targetFile)
	{
		File source = new File(sourceFile).getAbsoluteFile();
		File target = new File(targetFile).getAbsoluteFile();
		Path result = source.toPath().getParent().relativize(target.toPath());
		return result.toString();
	}
}
