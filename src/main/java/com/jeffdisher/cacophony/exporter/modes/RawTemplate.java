package com.jeffdisher.cacophony.exporter.modes;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Map;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;


public class RawTemplate
{
	public static RawTemplate filePathTemplate(String raw, String builtInMacros)
	{
		return new RawTemplate(raw, builtInMacros, null);
	}

	public static RawTemplate fileContentTemplate(String raw, File macroDirectory)
	{
		return new RawTemplate(raw, "", macroDirectory);
	}


	private final String _raw;
	private final File _macroDirectory;

	private RawTemplate(String raw, String builtInMacros, File macroDirectory)
	{
		_raw = builtInMacros + raw;
		_macroDirectory = macroDirectory;
	}

	public String processWithContext(String name, Map<String, Object> context) throws IOException, TemplateException
	{
		Configuration cfg = new Configuration(Configuration.VERSION_2_3_32);
		if (null != _macroDirectory)
		{
			cfg.setDirectoryForTemplateLoading(_macroDirectory);
		}
		Template template = new Template(name, _raw, cfg);
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		template.process(context, new OutputStreamWriter(bytes));
		return new String(bytes.toByteArray());
	}
}
