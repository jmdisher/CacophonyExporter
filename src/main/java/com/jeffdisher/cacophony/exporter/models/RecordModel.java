package com.jeffdisher.cacophony.exporter.models;

import java.util.Date;

import com.jeffdisher.cacophony.data.global.AbstractRecord;
import com.jeffdisher.cacophony.exporter.modes.RecordHelpers;
import com.jeffdisher.cacophony.types.IpfsFile;

import freemarker.template.ObjectWrapper;
import freemarker.template.SimpleDate;
import freemarker.template.SimpleHash;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;


public class RecordModel implements TemplateHashModel
{
	private final AbstractRecord _record;

	public RecordModel(AbstractRecord record)
	{
		_record = record;
	}

	@Override
	public TemplateModel get(String key) throws TemplateModelException
	{
		// We implemented this, instead of using the BeansWrapper, since we need to manually convert some types to strings but also to make sense of attachments.
		TemplateModel model;
		if ("name".equals(key))
		{
			model = new SimpleScalar(_record.getName());
		}
		else if ("description".equals(key))
		{
			model = new SimpleScalar(_record.getDescription());
		}
		else if ("discussionUrl".equals(key))
		{
			String discussionUrl = _record.getDiscussionUrl();
			if (null != discussionUrl)
			{
				model = new SimpleScalar(discussionUrl);
			}
			else
			{
				model = null;
			}
		}
		else if ("publisher".equals(key))
		{
			model = new SimpleScalar(_record.getPublisherKey().toPublicKey());
		}
		else if ("date".equals(key))
		{
			// We store seconds since epoch but the Date wants millis since epoch.
			model = new SimpleDate(new Date(_record.getPublishedSecondsUtc() * 1000L), SimpleDate.DATETIME);
		}
		else if ("thumbnail".equals(key))
		{
			IpfsFile cid = _record.getThumbnailCid();
			if (null != cid)
			{
				String mime = _record.getThumbnailMime();
				model = _createSubStructure(cid, mime);
			}
			else
			{
				model = null;
			}
		}
		else if ("audio".equals(key))
		{
			AbstractRecord.Leaf leaf = RecordHelpers.findAudio(_record);
			if (null != leaf)
			{
				model = _createSubStructure(leaf.cid(), leaf.mime());
			}
			else
			{
				model = null;
			}
		}
		else if ("video".equals(key))
		{
			AbstractRecord.Leaf leaf = RecordHelpers.findVideo(_record);
			if (null != leaf)
			{
				model = _createSubStructure(leaf.cid(), leaf.mime());
			}
			else
			{
				model = null;
			}
		}
		else if ("replyTo".equals(key))
		{
			IpfsFile cid = _record.getReplyTo();
			if (null != cid)
			{
				model = new SimpleScalar(cid.toSafeString());
			}
			else
			{
				model = null;
			}
		}
		else
		{
			// This is just a bogus key.
			throw new TemplateModelException("Unknown key: " + key);
		}
		return model;
	}

	@Override
	public boolean isEmpty() throws TemplateModelException
	{
		// These always have data.
		return false;
	}


	private SimpleHash _createSubStructure(IpfsFile cid, String mime)
	{
		SimpleHash hash = new SimpleHash((ObjectWrapper)null);
		hash.put("mime", mime);
		hash.put("cid", cid.toSafeString());
		return hash;
	}
}
