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

<portlet:resourceURL var="ajaxURL"></portlet:resourceURL>

<div class="homepageheading">
    License Debt
</div>
<div id="myLicenseDebtPortletDiv" class="homepageListingTable">
    <table id="myLicenseDebtTable" cellpadding="0" cellspacing="0" border="0" class="display">
         <colgroup>
               <col style="width: 70%;"/>
               <col style="width: 30%;"/>
         </colgroup>
    </table>


    <%--
    <div id="myLicenseDebtChartDiv">

        <div class="clearing_detail_controls">
            <aui:select label="Select Project:"
                        id="options"
                        name="selectProjectField"
                        required="true"
                        showEmptyOption="false"
                        inlineLabel="left">
            <core_rt:forEach items="${projects}" var="project">
            <aui:option value="${project.id}">${project.name}</aui:option>
            </core_rt:forEach>
            </aui:select>
            <aui:button value="Get Clearing Details" id="btnSubmit" cssClass="btn btn-primary"/>
        </div>
    </div>
    --%>

    <div id="licensedebtchart"></div>


</div>

<script>

    <%--
    <aui:script use="node, event">
    var btn = A.one( '#btnSubmit' )
    btn.on( 'click', function( event ){
        updateChartWithProjectId( document.getElementById( "<portlet:namespace/>options" ).value );
    })
    </aui:script>
    --%>

    Liferay.on( 'allPortletsReady', function() {
        let result = [];
        let projectClearingSummaryList = [];
        <core_rt:forEach items="${projects}" var="project">
        result.push({
            "DT_RowId": "${project.id}",
            "0": "<sw360:DisplayProjectLink project="${project}"/>",
            "1": '<sw360:out value="${project.releaseClearingStateSummary.approved} / ${project.releaseClearingStateSummary.newRelease + project.releaseClearingStateSummary.underClearing + project.releaseClearingStateSummary.underClearingByProjectTeam + project.releaseClearingStateSummary.reportAvailable + project.releaseClearingStateSummary.approved}" default="--"/>'
        });
        projectClearingSummaryList.push({
            "Project Name": "${project.name}",
            "Under Clearing By Project Team": ${project.releaseClearingStateSummary.underClearingByProjectTeam},
            "Under Clearing": ${project.releaseClearingStateSummary.underClearing},
            "New Release": ${project.releaseClearingStateSummary.newRelease},
            "Report Available": ${project.releaseClearingStateSummary.reportAvailable},
            "Approved": ${project.releaseClearingStateSummary.approved}
        });
        </core_rt:forEach>

        $( '#myLicenseDebtTable' ).dataTable({
            pagingType: "simple_numbers",
            dom: "rtip",
            data: result,
            pageLength: 5,
            columns: [
                {"title": "Project Name"},
                {"title": "Accepted Releases"}
            ],
            autoWidth: false
        });

        drawBarChart( projectClearingSummaryList );

        <%--
        let projectId = document.getElementById( "<portlet:namespace/>options" ).value;
        updateChartWithProjectId( projectId );
        --%>
    });

    function drawBarChart( data ) {


        let chartData = data.slice(1).slice(-5);

        console.log('chartData', chartData);

        let chartData2 = data.slice(Math.max(arr.length - 5));

        console.log('chartData2', chartData2);

        YUI().use('charts', function (Y) {
            var chart = new Y.Chart({
                render: "#licensedebtchart",
                dataProvider: chartData,
                categoryKey:"Project Name",
                type:"bar"
            });
        });
    }

    <%--
    YUI().use(
        'aui-progressbar',
        function(Y) {
            new Y.ProgressBar(
                {
                    boundingBox: '#myProgressBar',
                    value: 99,
                    width: 100,
                    height: 20
                }
            ).render();
        }
    );
    --%>

    function updateChartWithProjectId( projectId ) {
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
                if ( data.hasOwnProperty(e)){
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
            $.alert({
                title: "Could not fetch projects from backend",
                content: response,
                type: 'red'
            });
        });
    }

    function drawPieChart( points ) {
        YUI().use( 'charts', function(Y){
            var pieGraph = new Y.Chart({
                render: "#licensedebtchart",
                categoryKey: "status",
                seriesKeys: ["releases"],
                dataProvider: points,
                type:"pie",
                stacked:true,
                seriesCollection: [{
                    categoryKey: "status",
                    valueKey: "releases"
                }]
            });
        });
    }

</script>
