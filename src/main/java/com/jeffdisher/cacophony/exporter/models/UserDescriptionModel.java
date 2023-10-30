package com.jeffdisher.cacophony.exporter.models;

import com.jeffdisher.cacophony.data.global.AbstractDescription;
import com.jeffdisher.cacophony.types.IpfsFile;

import freemarker.template.ObjectWrapper;
import freemarker.template.SimpleHash;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;


public class UserDescriptionModel implements TemplateHashModel
{
	private final AbstractDescription _description;

	public UserDescriptionModel(AbstractDescription description)
	{
		_description = description;
	}

	@Override
	public TemplateModel get(String key) throws TemplateModelException
	{
		// We implemented this, instead of using the BeansWrapper, since we need to manually convert some types to strings but also to make sense of attachments.
		TemplateModel model;
		if ("name".equals(key))
		{
			model = new SimpleScalar(_description.getName());
		}
		else if ("description".equals(key))
		{
			model = new SimpleScalar(_description.getDescription());
		}
		else if ("email".equals(key))
		{
			String email = _description.getEmail();
			if (null != email)
			{
				model = new SimpleScalar(email);
			}
			else
			{
				model = null;
			}
		}
		else if ("website".equals(key))
		{
			String website = _description.getWebsite();
			if (null != website)
			{
				model = new SimpleScalar(website);
			}
			else
			{
				model = null;
			}
		}
		else if ("feature".equals(key))
		{
			IpfsFile feature = _description.getFeature();
			if (null != feature)
			{
				model = new SimpleScalar(feature.toSafeString());
			}
			else
			{
				model = null;
			}
		}
		else if ("userPic".equals(key))
		{
			IpfsFile cid = _description.getPicCid();
			if (null != cid)
			{
				String mime = _description.getPicMime();
				model = _createSubStructure(cid, mime);
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
