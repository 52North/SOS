<#-- To render the third-party file.
 Available context :

 - dependencyMap a collection of Map.Entry with
   key are dependencies (as a MavenProject) (from the maven project)
   values are licenses of each dependency (array of string)

 - licenseMap a collection of Map.Entry with
   key are licenses of each dependency (array of string)
   values are all dependencies using this license
-->
<#function licenseFormat licenses>
    <#assign result = ""/>
    <#list licenses as license>
        <#assign result = result + " (" +license + ")"/>
    </#list>
	<#assign result = result + "; "/>
    <#return result>
</#function>
<#function artifactFormat p>
    <#if p.name?index_of('Unnamed') &gt; -1>
        <#return p.artifactId + "; " + p.groupId + ":" + p.artifactId + ":" + p.version + "; " + (p.url!"no url defined")>
    <#else>
        <#return p.name + "; " + p.groupId + ":" + p.artifactId + ":" + p.version + "; " + (p.url!"no url defined")>
    </#if>
</#function>

<#if dependencyMap?size == 0>
The project has no dependencies.
<#else>
Lists of ${dependencyMap?size} third-party dependencies.
    <#list dependencyMap as e>
        <#assign project = e.getKey()/>
        <#assign licenses = e.getValue()/>
    ${licenseFormat(licenses)} ${artifactFormat(project)}
    </#list>
</#if>