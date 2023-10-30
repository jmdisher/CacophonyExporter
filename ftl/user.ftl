<#ftl output_format="HTML">
<#include "macros_commandline.ftl">
<#include "macros.ftl">
<html>
<meta charset="utf-8"></meta>
<meta name="viewport" content="width=device-width, initial-scale=1"></meta>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.0/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-gH2yIJqKdNHPEq0n4Mqa/HGKIhSkIHeL5AyhkYV8i59U5AR6csBvApHHNl/vI1Bx" crossorigin="anonymous"></link>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.2.0/dist/js/bootstrap.bundle.min.js" integrity="sha384-A3rJD856KowSb7dwlZdYEkO39Gagi7vIsF0jrRAoQmDKKtQBHUuLZ9AsSv4jD4Xa" crossorigin="anonymous"></script>
<head>
	<title>${user.name}</title>
</head>
<body>

<div class="container">
	<div class="row">
		<div class="col-md-12">
			<div class="row card">
				<h5 class="card-header">${user.name}</h5>
				<div class="card-body container row">
					<div class="col-md-4">
<#if user.userPic??>
<#if userPicPath??>
						<img class="img-fluid" src="${userPicPath}" />
<#else>
						User pic only available with <a href="https://github.com/jmdisher/Cacophony">local Cacophony</a>
</#if>
</#if>
					</div>
					<div class="col-md-8">
						<div class="card">
							<h5 class="card-header">Public Key:</h5>
							<div class="card-body">${publicKey}</div>
						</div>
<#if user.email??>
						<div class="card">
							<h5 class="card-header">Email:</h5>
							<div class="card-body"><a href="mailto:${user.email}">${user.email}</a></div>
						</div>
</#if>
<#if user.website??>
						<div class="card">
							<h5 class="card-header">Website:</h5>
							<div class="card-body"><a href="${user.website}">${user.website}</a></div>
						</div>
</#if>
						<div class="card">
							<h5 class="card-header">Description:</h5>
							<div class="card-body" id="description">
								<@newlines raw="${user.description}" />
							</div>
						</div>
					</div>
				</div>
			</div>
			
<#list recordCids?reverse as cid>
			<div class="row card">
				<h5 class="card-header">${recordMap[cid].name} (on ${recordMap[cid].date})</h5>
				<div class="card-body container row">
					<div class="col-md-3">
<#if recordMap[cid].thumbnail?? && shouldShowThumbnails>
						<a href="../record_${cid}/index.html"><img class="img-fluid" src="../record_${cid}/thumbnail.<@ext mime="${recordMap[cid].thumbnail.mime}" />" alt="${recordMap[cid].name}" /></a>
<#else>
						<a href="../record_${cid}/index.html">${recordMap[cid].name}</a>
</#if>
					</div>
					<div class="col-md-9">
						<@newlines raw="${recordMap[cid].description?truncate_w(255)}" />
					</div>
				</div>
			</div>
</#list>
		</div>
	</div>

</body>
</html>

