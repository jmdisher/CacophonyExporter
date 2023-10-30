#!/bin/bash

# A simple example of how to run this on the "Cacophony Explained" channel.

java -jar -Xmx8m CacophonyExporter.jar \
	--user z5AanNVJCxnJ6qSdFeWsMDaivGJPPCVx8jiopn9jK7aUThhuQjhERku \
	--pathMacroFile ftl/macros_commandline.ftl \
	--saveUsers ftl/user.ftl "output/user_$""{publicKey}/index.html" \
	--saveRecords ftl/record.ftl "output/record_$""{cid}/index.html" \
	--saveUserPic "output/user_$""{publicKey}/image.<@ext mime=\"$""{user.userPic.mime}\" />" \
	--saveThumbnail "output/record_$""{cid}/thumbnail.<@ext mime=\"$""{record.thumbnail.mime}\" />" \
	--saveAudio "output/record_$""{cid}/audio.<@ext mime=\"$""{record.audio.mime}\" />" \
	--saveVideo "output/record_$""{cid}/video.<@ext mime=\"$""{record.video.mime}\" />" \
	--includeReplyTo \
	--includeReferencedUsers

