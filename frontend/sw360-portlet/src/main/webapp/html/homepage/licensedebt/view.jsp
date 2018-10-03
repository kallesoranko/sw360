<%--
  ~ Copyright Siemens AG, 2013-2017. Part of the SW360 Portal Project.
  ~
  ~ SPDX-License-Identifier: EPL-1.0
  ~
  ~ All rights reserved. This program and the accompanying materials
  ~ are made available under the terms of the Eclipse Public License v1.0
  ~ which accompanies this distribution, and is available at
  ~ http://www.eclipse.org/legal/epl-v10.html
  --%>

<%@include file="/html/init.jsp" %>
<portlet:defineObjects/>
<liferay-theme:defineObjects/>

<jsp:useBean id="projects" type="java.util.List<org.eclipse.sw360.datahandler.thrift.projects.Project>"
             scope="request"/>

<div class="homepageheading">
    License Debt
</div>
<div id="myProjectsDiv" class="homepageListingTable">
    <table id="myProjectsTable" cellpadding="0" cellspacing="0" border="0" class="display">
         <colgroup>
               <col style="width: 50%;"/>
               <col style="width: 50%;"/>
         </colgroup>
    </table>
</div>

<script>

    Liferay.on('allPortletsReady', function() {
        var result = [];

        <core_rt:forEach items="${projects}" var="project">
        result.push({
            "DT_RowId": "${project.id}",
            "0": "<sw360:DisplayProjectLink project="${project}"/>",
            "1": '<sw360:out value="${project.clearingState}"/>'
        });
        </core_rt:forEach>

        $('#myProjectsTable').dataTable({
            pagingType: "simple_numbers",
            dom: "rtip",
            data: result,
            pageLength: 3,
            columns: [
                {"title": "Project Name"},
                {"title": "Clearing State"},
            ],
            autoWidth: false
        });
    });

</script>
