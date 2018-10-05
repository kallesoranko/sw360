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
<portlet:resourceURL var="ajaxURL" id="view.jsp"></portlet:resourceURL>
<liferay-theme:defineObjects/>

<jsp:useBean id="projects" type="java.util.List<org.eclipse.sw360.datahandler.thrift.projects.Project>"
             scope="request"/>

<div class="homepageheading">
    License Debt
</div>
<div id="myLicenseDebtDiv" class="homepageListingTable">
    <table id="myLicenseDebtTable" cellpadding="0" cellspacing="0" border="0" class="display">
         <colgroup>
               <col style="width: 50%;"/>
               <col style="width: 50%;"/>
         </colgroup>
    </table>

    <div id="header">
        <aui:select label="Select Project:" id="options" name="selectField1" required="true" showEmptyOption="false" inlineLabel="left">
        <core_rt:forEach items="${projects}" var="project">
        <aui:option value="${project.id}" cssClass="test_content1">"${project.name}"</aui:option>
        </core_rt:forEach>
        </aui:select>
        <span class="toplabelledInput">
            <%-- do we use aui:button or input --%>
            <input type="button" id="edit" value="Edit" class="addButton">

            <aui:button value="Submit" id="btnSubmit" cssClass="test_content2"/>
        </span>
    </div>

    <aui:script use="node, event">
        <link rel="stylesheet" href="<%=request.getContextPath()%>/webjars/github-com-craftpip-jquery-confirm/3.0.1/jquery-confirm.min.css">

        var btn = A.one('#btnSubmit');
        var option = A.one('#<portlet:namespace/>options')
        btn.on('click', function(event){
            var myProject = option.val();
            alert(myProject);
            updatePieChartWithProjectId(myProject);
        });

        YUI().use('charts', function(Y){



            var myDataValues = [
                {day:"Monday", taxes:2000},
                {day:"Tuesday", taxes:50},
                {day:"Wednesday", taxes:4000},
                {day:"Thursday", taxes:200},
                {day:"Friday", taxes:2000}
            ];
            var pieGraph = new Y.Chart({
                render:"#licensedebtchart",
                categoryKey:"day",
                seriesKeys:["taxes"],
                dataProvider:myDataValues,
                type:"pie",
                seriesCollection:[{
                    categoryKey:"day",
                    valueKey:"taxes"
                }]
            });
        });

        function updatePieChartWithProjectId(projectId) {
            $.alert({
                title: "Project ID",
                content: projectId
            });

            $.ajax({
                url: '<%=ajaxURL%>',
                type: 'POST',
                cache: false,
                dataType: 'json',
                data: {
                    "<portlet:namespace/><%=ProjectImportConstants.USER_ACTION__UPDATE%>":
                        "<%=ProjectImportConstants.USER_ACTION__UPDATE_CHART%>",
                    "<portlet:namespace/><%=ProjectImportConstants.PROJECT_id%>":
                        projectId
                }
            }).done(response => { console.log(response) }
            ).fail( () => { console.log('ERROR') }
            );
        }
     </aui:script>

    <div id="licensedebtchart"></div>

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

        $('#myLicenseDebtTable').dataTable({
            pagingType: "simple_numbers",
            dom: "rtip",
            data: result,
            pageLength: 5,
            columns: [
                {"title": "Project Name"},
                {"title": "Clearing State"},
            ],
            autoWidth: false
        });
    });



</script>
