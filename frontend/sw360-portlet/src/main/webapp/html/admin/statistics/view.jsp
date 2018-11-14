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
        <table id="details-table" cellpadding="0" cellspacing="0" border="0" class="display">
             <colgroup>
                   <col style="width: 40%;"/>
                   <col style="width: 30%;"/>
                   <col style="width: 30%;"/>
             </colgroup>
        </table>
    </div>
</div>

<div id="component-type-chart-div">
    <h2>component-type-chart-div</h2>
</div>

<link rel="stylesheet" href="<%=request.getContextPath()%>/webjars/jquery-ui/1.12.1/jquery-ui.css">
<link rel="stylesheet" href="<%=request.getContextPath()%>/webjars/github-com-craftpip-jquery-confirm/3.0.1/jquery-confirm.min.css">
<script src="<%=request.getContextPath()%>/webjars/jquery/1.12.4/jquery.min.js" type="text/javascript"></script>
<script src="<%=request.getContextPath()%>/webjars/jquery-ui/1.12.1/jquery-ui.min.js" type="text/javascript"></script>
<script src="<%=request.getContextPath()%>/webjars/github-com-craftpip-jquery-confirm/3.0.1/jquery-confirm.min.js" type="text/javascript"></script>

<script src="https://code.highcharts.com/highcharts.js"></script>
<script src="https://code.highcharts.com/modules/exporting.js"></script>

<link href="<%=request.getContextPath()%>/js/libs/c3.min.css" rel="stylesheet">
<script src="<%=request.getContextPath()%>/js/libs/d3.min.js" charset="utf-8"></script>
<script src="<%=request.getContextPath()%>/js/libs/c3.min.js"></script>

<script>

    Liferay.on( 'allPortletsReady', function() {
        var projectInfo = [];
        var series = [{
            name: 'Approved',
            data: []
        }, {
            name: 'Report Available',
            data: []
        }, {
            name: 'New Release',
            data: []
        }, {
            name: 'Under Clearing',
            data: []
        }, {
            name: 'Under Clearing By Project Team',
            data: []
        }];

        <core_rt:forEach items="${projects}" var="project">
        projectInfo.push({
            name: "${project.name}",
            id: "${project.id}",
            releaseIdToUsage: "${project.releaseIdToUsage}"
        });
        series[0].data.push(${project.releaseClearingStateSummary.approved});
        series[1].data.push(${project.releaseClearingStateSummary.reportAvailable});
        series[2].data.push(${project.releaseClearingStateSummary.newRelease});
        series[3].data.push(${project.releaseClearingStateSummary.underClearing});
        series[4].data.push(${project.releaseClearingStateSummary.underClearingByProjectTeam});
        </core_rt:forEach>

        drawChart(projectInfo, series);
    });

    function drawChart(projectInfo, series) {

        Highcharts.chart('licensedebtchart', {
            chart: {
                type: 'bar'
            },
            title: {
                text: 'License debt'
            },
            xAxis: {
                categories: projectInfo.map( e => e.name )
            },
            yAxis: {
                min: 0,
                title: {
                    text: 'No. Releases'
                }
            },
            legend: {
                reversed: true
            },
            plotOptions: {
                series: {
                    stacking: 'normal',
                    pointWidth: 22,
                    events: {
                        click: function (event) {
                            console.log('this', this);
                            console.log('event', event);

                            projectInfo.forEach( e => {
                                if (e.name ===  event.point.category) {
                                    console.log('e.name', e.name);
                                    console.log('e.id', e.id);

                                    loadProjectDetails(e.id, e.releaseIdToUsage);

                                }
                            });
                        }
                    }
                }
            },
            series: series
        });
    }

    function loadProjectDetails(projectId, releaseIdToUsage){
        $.ajax({
            url: '<%=ajaxURL%>',
            type: 'POST',
            cache: false,
            dataType: 'json',
            data: {
                "<portlet:namespace/><%=PortalConstants.PROJECT_ID%>" :  projectId,
                "<portlet:namespace/><%=PortalConstants.RELEASE_ID_TO_USAGE%>" :  releaseIdToUsage
            }
        }).done(function(response){
            console.log(response);
        });
    }

</script>