package com.jeffdisher.cacophony.exporter.modes;

import com.jeffdisher.cacophony.exporter.models.SoftRecordMapModel;
import com.jeffdisher.cacophony.exporter.models.LazyUserDescriptionMapModel;
import com.jeffdisher.cacophony.types.IConnection;


public record Environment(IConnection cacophonyConnection
		, SoftRecordMapModel recordMap
		, LazyUserDescriptionMapModel userMap
		, boolean includeReplyTo
		, boolean includeReferencedUsers
		
		, RawTemplate content_userTemplateString
		, RawTemplate path_userPathTemplate
		, RawTemplate path_userPicPathTemplate
		
		, RawTemplate content_recordTemplateString
		, RawTemplate path_recordPathTemplate
		, RawTemplate path_thumbnailPathTemplate
		, RawTemplate path_audioPathTemplate
		, RawTemplate path_videoPathTemplate
){}
