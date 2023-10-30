<#ftl output_format="HTML">
<#include "macros_commandline.ftl">
<#include "macros.ftl">
<html>
<meta charset="utf-8"></meta>
<meta name="viewport" content="width=device-width, initial-scale=1"></meta>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.0/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-gH2yIJqKdNHPEq0n4Mqa/HGKIhSkIHeL5AyhkYV8i59U5AR6csBvApHHNl/vI1Bx" crossorigin="anonymous"></link>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.2.0/dist/js/bootstrap.bundle.min.js" integrity="sha384-A3rJD856KowSb7dwlZdYEkO39Gagi7vIsF0jrRAoQmDKKtQBHUuLZ9AsSv4jD4Xa" crossorigin="anonymous"></script>
<head>
	<title>${record.name}</title>
</head>
<body>

<div class="container">
	<div class="jumbotron">
<#if record.video??>
		<div class="row card card-body">
<#if relativePaths.video??>
			<center><video src="${relativePaths.video}" controls /></center>
<#else>
			Video only available with <a href="https://github.com/jmdisher/Cacophony">local Cacophony</a>
</#if>
		</div>
</#if>
<#if record.thumbnail?? && !record.video??>
		<div class="row card card-body">
<#if relativePaths.thumbnail??>
			<img class="img-responsive" src="${relativePaths.thumbnail}" alt="${record.name}" />
<#else>
			Thumbnail only visible with <a href="https://github.com/jmdisher/Cacophony">local Cacophony</a>
</#if>
		</div>
</#if>
<#if record.audio??>
		<div class="row card card-body">
<#if relativePaths.audio??>
			<center><audio src="${relativePaths.audio}" controls /></center>
<#else>
			Audio only available with <a href="https://github.com/jmdisher/Cacophony">local Cacophony</a>
</#if>
		</div>
</#if>
		<div class="row card card-body">
			<strong>${record.name}</strong><br />
<#if userMap[record.publisher]??>
			<span>Posted by <a href="../user_${record.publisher}/index.html">${userMap[record.publisher].name}</a></span><br />
<#else>
			<span>Posted by unknown user:  ${record.publisher}</span><br />
</#if>
<#if record.replyTo??>
			<span>Reply to:  <a href="../record_${record.replyTo}/index.html">${recordMap[record.replyTo].name}</a> 
<#if userMap[recordMap[record.replyTo].publisher]??>
			(Posted by Posted by <a href="../user_${recordMap[record.replyTo].publisher}/index.html">${userMap[recordMap[record.replyTo].publisher].name}</a>)
<#else>
			(Posted by Posted by unknown user:  ${recordMap[record.replyTo].publisher})
</#if>
			</span><br />
</#if>
		</div>
	</div>
	<div class="row justify-content-md-center">
		<div class="col-md-6">
			<div class="row card">
				<h5 class="card-header">${record.name}</h5>
				<div class="card-body">
					<@newlines raw="${record.description}" />
				</div>
			</div>
<#if record.discussionUrl??>
			<div class="row card card-body">
				<a href="${record.discussionUrl}">View discussion: ${record.discussionUrl}</a>
			</div>
</#if>
		</div>
	</div>
</div>

</body>
</html>

