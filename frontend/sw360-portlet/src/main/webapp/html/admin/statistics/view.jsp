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

<jsp:useBean id="projects" type="java.util.List<org.eclipse.sw360.datahandler.thrift.projects.Project>" scope="request"/>

<%--
<jsp:useBean id="releases" type="java.util.List<org.eclipse.sw360.datahandler.thrift.components.Release>" scope="request"/>
--%>

<portlet:resourceURL var="ajaxURL"></portlet:resourceURL>

<div id="header"></div>
<p class="pageHeader">
    <span class="pageHeaderBigSpan">Statistics</span>
    <span class="pull-right"></span>
</p>

<div id="statistics-div">
    <div id="statistics-chart-div">
        <div id="licensedebtchart"></div>
    </div>

    <div id="statistics-details-div">
        <p><span class="pageHeaderBigSpan" id="table-header"></span></p>
        <table id="details-table" cellpadding="0" cellspacing="0" border="0" class="display">
             <colgroup>
                   <col style="width: 40%;"/>
                   <col style="width: 30%;"/>
                   <col style="width: 30%;"/>
             </colgroup>
        </table>
    </div>
</div>

<%--
<div id="component-type-chart-div">
    <h2>component-type-chart-div</h2>
</div>
--%>

<link rel="stylesheet" href="<%=request.getContextPath()%>/css/dataTable_Siemens.css">
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/sw360.css">
<link rel="stylesheet" href="<%=request.getContextPath()%>/webjars/jquery-ui/1.12.1/jquery-ui.css">
<link rel="stylesheet" href="<%=request.getContextPath()%>/webjars/github-com-craftpip-jquery-confirm/3.0.1/jquery-confirm.min.css">

<%--
<script src="<%=request.getContextPath()%>/webjars/jquery/1.12.4/jquery.min.js" type="text/javascript"></script>
<script src="<%=request.getContextPath()%>/webjars/jquery-ui/1.12.1/jquery-ui.min.js" type="text/javascript"></script>
<script src="<%=request.getContextPath()%>/webjars/datatables/1.10.15/js/jquery.dataTables.min.js" type="text/javascript"></script>
<script src="<%=request.getContextPath()%>/webjars/datatables.net-select/1.2.2/js/dataTables.select.min.js" type="text/javascript"></script>
<script src="<%=request.getContextPath()%>/webjars/github-com-craftpip-jquery-confirm/3.0.1/jquery-confirm.min.js" type="text/javascript"></script>
--%>

<link href="<%=request.getContextPath()%>/js/libs/c3.min.css" rel="stylesheet">
<script src="<%=request.getContextPath()%>/js/libs/c3.min.js"></script>

<script src="<%=request.getContextPath()%>/js/libs/d3.min.js" charset="utf-8"></script>

<script>
    let detailsTable;
    function str2DOMElement(html) {
        var frame = document.createElement('iframe');
        frame.style.display = 'none';
        document.body.appendChild(frame);
        frame.contentDocument.open();
        frame.contentDocument.write(html);
        frame.contentDocument.close();
        var el = frame.contentDocument.body.firstChild;
        document.body.removeChild(frame);
        return el;
    }
    function html2dom( html ) {
        var container = document.createElement('div');
        container.innerHTML = html;
        return container.firstChild;
    }
    Liferay.on( 'allPortletsReady', function() {
        var projectInfoList = [];
        var series = {
            approved: ['Approved'],
            reportAvailable: ['Report Available'],
            newRelease: ['New Release'],
            underClearing: ['Under Clearing'],
            underClearingByProjectTeam: ['Under Clearing By Project Team'],
            projectName: ['x']
        }
        <core_rt:forEach items="${projects}" var="project">
        projectInfoList.push({
            name: "${project.name}",
            id: "${project.id}",
            releaseIds: "${project.releaseIdToUsage.keySet()}".toString().slice(1, -1).split(',').map(e=>e.trim())
        });
        series.approved.push(${project.releaseClearingStateSummary.approved});
        series.reportAvailable.push(${project.releaseClearingStateSummary.reportAvailable});
        series.newRelease.push(${project.releaseClearingStateSummary.newRelease});
        series.underClearing.push(${project.releaseClearingStateSummary.underClearing});
        series.underClearingByProjectTeam.push(${project.releaseClearingStateSummary.underClearingByProjectTeam});
        series.projectName.push("${project.name}");
        </core_rt:forEach>
        drawChart(projectInfoList, series);
        loadProjectDetails(projectInfoList[0]);
    });
    <%--
    c3.chart.internal.fn.selectPath = function (target, d) {
        var rubid = Number(d.id.substring(d.id.length - 1));
        if (rubid == d.index) {
            var brightness = 0;
        } else {
            var brightness = 1.75;
        }
        var $$ = this;
        $$.config.data_onselected.call($$, d, target.node());
        target.transition().duration(100)
            .style("fill", function () { return $$.d3.rgb($$.color(d)).brighter(brightness); });
    };
    --%>
    function drawChart(projectInfoList, series) {
        var chart = c3.generate({
            bindto: '#licensedebtchart',
            data: {
                columns: [
                    series.approved,
                    series.reportAvailable,
                    series.underClearing,
                    series.underClearingByProjectTeam,
                    series.newRelease
                ],
                type: 'bar',
                groups: [
                    ['Approved', 'Report Available', 'Under Clearing', 'Under Clearing By Project Team', 'New Release']
                ],
                onclick: function (d, i) {
                    loadProjectDetails(projectInfoList[d.x]);
                },
                selection: {
                    enabled: true
                },
                colors: {
                    'Approved': '#91C09E',
                    'Report Available': '#7B786B',
                    'Under Clearing': '#EAC761',
                    'Under Clearing By Project Team': '#E8DF9C',
                    'New Release': '#F75E50'
                },
            },
            grid: {
                y: {
                    show: true,
                    lines: [{value:0}]
                }
            },
            bar: {
                width: {
                    ratio: 0.85
                }
            },
            axis: {
                rotated: true,
                x: {
                    type: 'category',
                    categories: projectInfoList.map(e => e.name)
                }
            },
            zoom: {
                enabled: true
            }
        });
    }
    function loadProjectDetails(projectInfo){
        $.ajax({
            url: '<%=ajaxURL%>',
            type: 'POST',
            cache: false,
            dataType: 'json',
            data: {
                "<portlet:namespace/><%=PortalConstants.PROJECT_ID%>": projectInfo.id,
                "<portlet:namespace/>ids": projectInfo.releaseIds
            }
        }).done( function(response) {
            console.log(response);
            populateDetailsDiv(projectInfo, response.response_project_details_data);
        });
    }
    function populateDetailsDiv(projectInfo, releases) {
        document.getElementById("table-header").innerHTML = projectInfo.name.toString();
        var data = [];
        releases.forEach( e => {
            data.push({
                <%--
                var markup = "<sw360:DisplayReleaseLink releaseId="${e.id}" showName="true"/>";
                var parser = new DOMParser();
                let doc = new DOMParser().parseFromString('<div>first div content</div><div>second div content</div>', 'text/html');
                let firstDiv = doc.body.firstChild;
                let secondDiv = firstDiv.nextSibling;
                var doc = new DOMParser().parseFromString(<sw360:DisplayReleaseLink releaseId="${e.id}" showName="true"/>, 'text/html');
                var el = doc.body.firstChild;
                --%>
                "DT_RowId": e.id,
                "0": e.name,
                "1": e.clearingState
            });
        });
        detailsTable = $('#details-table').DataTable({
            pagingType: "simple_numbers",
            dom: "rtip",
            data: data,
            pageLength: 17,
            columns: [
                {"title": "Release Name"},
                {"title": "Clearing State"},
            ],
            autoWidth: false,
            "destroy": true,
            "createdRow": function( row, data, dataIndex ) {
                if ( data["1"] == "APPROVED" ) {
                    $( row ).css( "background-color", "#91C09E" );
                }
                if ( data["1"] == "REPORT_AVAILABLE" ) {
                    $( row ).css( "background-color", "#7B786B" );
                }
                if ( data["1"] == "UNDER_CLEARING" ) {
                    $( row ).css( "background-color", "#EAC761" );
                }
                if ( data["1"] == "UNDER_CLEARING_BY_PROJECT_TEAM" ) {
                    $( row ).css( "background-color", "#E8DF9C" );
                }
                if ( data["1"] == "NEW_CLEARING" ) {
                    $( row ).css( "background-color", "#F75E50" );
                }
            }
        });
    }
</script>


