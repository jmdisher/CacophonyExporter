package com.jeffdisher.cacophony.exporter.models;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.jeffdisher.cacophony.data.global.AbstractDescription;
import com.jeffdisher.cacophony.data.global.AbstractIndex;
import com.jeffdisher.cacophony.data.global.AbstractRecords;
import com.jeffdisher.cacophony.types.FailedDeserializationException;
import com.jeffdisher.cacophony.types.IConnection;
import com.jeffdisher.cacophony.types.IpfsConnectionException;
import com.jeffdisher.cacophony.types.IpfsFile;
import com.jeffdisher.cacophony.types.IpfsKey;
import com.jeffdisher.cacophony.utils.Assert;

import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;


/**
 * A map from public keys to user info.  Note that this will only try to load the list of users provided as a whitelist
 * and can return null from a lookup for a given key if the load failed or the user wasn't on the whitelist.
 */
public class LazyUserDescriptionMapModel implements TemplateHashModel
{
	private final IConnection _connection;
	private final Map<IpfsKey, AbstractRecords> _recordsMap;
	private final Map<IpfsKey, AbstractDescription> _descriptionMap;
	private final Set<IpfsKey> _failedLoads;

	public LazyUserDescriptionMapModel(IConnection connection)
	{
		_connection = connection;
		_recordsMap = new HashMap<>();
		_descriptionMap = new HashMap<>();
		_failedLoads = new HashSet<>();
	}

	@Override
	public TemplateModel get(String key) throws TemplateModelException
	{
		IpfsKey publicKey = IpfsKey.fromPublicKey(key);
		Assert.assertTrue(null != publicKey);
		
		AbstractDescription user = _lazyLoadUser(publicKey);
		return (null != user)
				? new UserDescriptionModel(user)
				: null
		;
	}

	@Override
	public boolean isEmpty() throws TemplateModelException
	{
		// These are never empty - they can fail to load, though.
		return false;
	}

	public AbstractDescription getDescription(IpfsKey publicKey)
	{
		return _lazyLoadUser(publicKey);
	}

	public AbstractRecords getRecords(IpfsKey publicKey)
	{
		AbstractDescription user = _lazyLoadUser(publicKey);
		// If the user was non-null, we know that records exists.
		return (null != user)
				? _recordsMap.get(publicKey)
				: null
		;
	}


	private AbstractDescription _lazyLoadUser(IpfsKey publicKey)
	{
		AbstractDescription user = _descriptionMap.get(publicKey);
		if ((null == user) && !_failedLoads.contains(publicKey))
		{
			// Lazily load this and populate it.
			try
			{
				System.out.println("Resolving user: " + publicKey.toPublicKey() + "...");
				IpfsFile root = _connection.resolve(publicKey);
				System.out.println("Resolved as: " + root.toSafeString());
				
				// We will load the index to get both the records and description - we store the records first since we check the description to see if this loaded.
				AbstractIndex index = AbstractIndex.DESERIALIZER.apply(_connection.loadData(root));
				AbstractRecords records = AbstractRecords.DESERIALIZER.apply(_connection.loadData(index.recordsCid));
				_recordsMap.put(publicKey, records);
				user = AbstractDescription.DESERIALIZER.apply(_connection.loadData(index.descriptionCid));
				_descriptionMap.put(publicKey, user);
			}
			catch (FailedDeserializationException | IpfsConnectionException e)
			{
				// Failure to resolve this information is a failure we just want to handle as a null.
				_failedLoads.add(publicKey);
			}
		}
		return user;
	}
}
