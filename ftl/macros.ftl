<#-- The FTL output_format is set in the parent template but not in the macro file so we need to manually set that here (this is a good thing since we are modifying the HTML here) -->
<#macro newlines raw>
	<#assign captured><#outputformat "XML">${raw}</#outputformat></#assign>
	${captured?replace("\n", "<br />\n")}
</#macro>

