package com.jeffdisher.cacophony.exporter.models;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

import com.jeffdisher.cacophony.data.global.AbstractRecord;
import com.jeffdisher.cacophony.types.FailedDeserializationException;
import com.jeffdisher.cacophony.types.IConnection;
import com.jeffdisher.cacophony.types.IpfsConnectionException;
import com.jeffdisher.cacophony.types.IpfsFile;
import com.jeffdisher.cacophony.utils.Assert;

import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;


/**
 * We use soft references for the record map since the records could be quite large (over 100 KiB) and we don't
 * typically reuse them but do, sometimes.
 */
public class SoftRecordMapModel implements TemplateHashModel
{
	private final IConnection _connection;
	private final Map<IpfsFile, SoftReference<RecordModel>> _map;

	public SoftRecordMapModel(IConnection connection)
	{
		_connection = connection;
		_map = new HashMap<>();
	}

	@Override
	public TemplateModel get(String key) throws TemplateModelException
	{
		IpfsFile cid = IpfsFile.fromIpfsCid(key);
		Assert.assertTrue(null != cid);
		SoftReference<RecordModel> ref = _map.get(cid);
		RecordModel record = (null != ref) ? ref.get() : null;
		if (null == record)
		{
			// Lazily load this and populate it.
			try
			{
				AbstractRecord elt = AbstractRecord.DESERIALIZER.apply(_connection.loadData(cid));
				record = new RecordModel(elt);
			}
			catch (FailedDeserializationException | IpfsConnectionException e)
			{
				// This can happen if this is a bogus CID or the node doesn't have this record (the tool probably shouldn't be used this way but is acceptable).
				throw new TemplateModelException("Failed to load record", e);
			}
			_map.put(cid, new SoftReference<>(record));
		}
		return record;
	}

	@Override
	public boolean isEmpty() throws TemplateModelException
	{
		// These are never empty - they can fail to load, though.
		return false;
	}
}
