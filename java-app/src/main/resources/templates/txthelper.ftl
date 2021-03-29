Constituency Document
=====================

<#list summary as k, v>
${k} : ${v}
</#list>

=====================

<#list candidates as map>
<#list map?keys as key>
<#if map[key]??>
${key} : ${map[key]}
<#else>
${key}-
</#if>
</#list>

=====================

</#list>
