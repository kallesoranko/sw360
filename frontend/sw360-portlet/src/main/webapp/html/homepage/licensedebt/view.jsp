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
<%-- the following is needed by liferay to display error messages--%>
<%@ include file="/html/utils/includes/errorKeyToMessage.jspf"%>
<%@ page import="org.eclipse.sw360.portal.common.PortalConstants" %>

<portlet:defineObjects/>
<liferay-theme:defineObjects/>

<jsp:useBean id="projects" type="java.util.List<org.eclipse.sw360.datahandler.thrift.projects.Project>"
             scope="request"/>

<portlet:resourceURL var="ajaxURL">
    <portlet:param name="<%=PortalConstants.ACTION%>" value='<%=PortalConstants.UPDATE_PIECHART%>'/>
</portlet:resourceURL>

<div class="homepageheading">
    License Debt
</div>
<div id="myLicenseDebtPortletDiv" class="homepageListingTable">
    <table id="myLicenseDebtTable" cellpadding="0" cellspacing="0" border="0" class="display">
         <colgroup>
               <col style="width: 60%;"/>
               <col style="width: 40%;"/>
         </colgroup>
    </table>

    <div id="myLicenseDebtDiv">
        <div class="clearing_detail_controls">
            <aui:select label="Select Project:"
                        id="options"
                        name="selectProjectField"
                        required="true"
                        showEmptyOption="false"
                        inlineLabel="left"
                        onChange="<portlet:namespace />updateView();">
            <core_rt:forEach items="${projects}" var="project">
            <aui:option value="${project.id}">${project.name}</aui:option>
            </core_rt:forEach>
            </aui:select>

            <aui:button value="Get Clearing Details" id="btnSubmit" cssClass="btn btn-primary"/>

<%--
            <aui:script use="node, event">
                function <portlet:namespace />updatePieChart() {
                    var sel = document.getElementById("<portlet:namespace/>options");
                    document.getElementById("<portlet:namespace/>key").value = sel.options[sel.selectedIndex].value;
                    console.log('sel', sel );
                    console.log('sel.value', sel.value);
                    updatePieChartWithProjectId(sel.value);
                }
            </aui:script>
--%>

        </div>
        <div id="licensedebtchart"></div>
    </div>

</div>

<script>

    function <portlet:namespace />updateView() {
        $.alert({
            title: "Jee",
            content: '...'
        });
    }

    <%--<aui:script use="node, event">--%>

    <%-- let option = A.one( '#<portlet:namespace/>options' ); --%>

<%--
    $('#options').on( 'change', function () {
        console.log('$("#options").on( "change....');
        let projectId = $(this).val();
        console.log(projectId);
        updatePieChartWithProjectId(projectId);
    });
--%>

<%--
    var dropDown = A.one('#selectField1');
    dropDown.on('change', function(){
        let projectId = document.getElementById("<portlet:namespace/>options").value;
        console.log('dropDown.on("change"....');
        console.log(projectId);
        updatePieChartWithProjectId(projectId);
    });
--%>

<%--
    document.getElementById("<portlet:namespace/>options").value.on('change', function(){
            let projectId = document.getElementById("<portlet:namespace/>options").value;
            console.log('dropDown.on("change"....');
            console.log(projectId);
            updatePieChartWithProjectId(projectId);
        });
--%>

<%--
    $("body").on('change', '#options', function(){
        let projectId = document.getElementById("<portlet:namespace/>options").value;
        console.log('dropDown.on("change"....');
        console.log(projectId);
        updatePieChartWithProjectId(projectId);
    });
--%>

    <%--</aui:script>--%>

    <aui:script use="node, event">
    var btn = A.one( '#btnSubmit' )
    btn.on('click', function(event){
        updatePieChartWithProjectId(document.getElementById( "<portlet:namespace/>options" ).value);
    })
    </aui:script>

    Liferay.on('allPortletsReady', function() {
        let result = [];
        <core_rt:forEach items="${projects}" var="project">
        result.push({
            "DT_RowId": "${project.id}",
            "0": "<sw360:DisplayProjectLink project="${project}"/>",
            "1": '<sw360:out value="${project.releaseClearingStateSummary.approved} / ${project.releaseClearingStateSummary.newRelease + project.releaseClearingStateSummary.underClearing + project.releaseClearingStateSummary.underClearingByProjectTeam + project.releaseClearingStateSummary.reportAvailable + project.releaseClearingStateSummary.approved}" default="--"/>'
        });
        </core_rt:forEach>

        $('#myLicenseDebtTable').dataTable({
            pagingType: "simple_numbers",
            dom: "rtip",
            data: result,
            pageLength: 5,
            columns: [
                {"title": "Project Name"},
                {"title": "Accepted Releases"},
            ],
            autoWidth: false
        });

        let projectId = document.getElementById( "<portlet:namespace/>options" ).value;
        console.log(projectId);
        updatePieChartWithProjectId(projectId);
    });

    function updatePieChartWithProjectId(projectId) {
        $.ajax({
            url: '<%=ajaxURL%>',
            type: 'POST',
            cache: false,
            dataType: 'json',
            data: {
                "<portlet:namespace/><%=PortalConstants.PROJECT_ID%>" :  projectId
            }
        }).done(function( response ) {
            let data = {};
            response.response_project_clearing_status_data.forEach(function(e) {
                if (data.hasOwnProperty(e)){
                    data[e]++;
                } else {
                    data[e] = 1;
                }
            });
            let datapoints = [];
            $.each( data, function( key, value ) {
                datapoints.push({
                    status: key,
                    releases: value
                });
            });
            drawPieChart( datapoints );
        }).fail(function( response ) {
            console.log( 'ERROR', response );
        });
    }

    function drawPieChart(points) {
        YUI().use('charts', function(Y){
            var pieGraph = new Y.Chart({
                render: "#licensedebtchart",
                categoryKey: "status",
                seriesKeys: ["releases"],
                dataProvider: points,
                type: "pie",
                seriesCollection: [{
                    categoryKey: "status",
                    valueKey: "releases"
                }]
            });
        });
    }

</script>
