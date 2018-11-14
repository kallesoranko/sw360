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

<div id="license-debt-div">
    <div id="statistics-chart-div">
        <div id="licensedebtchart"></div>
    </div>

    <div id="statistics-details-div">
        <h2>details-div</h2>
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



<%--
<script src="//cdn.jsdelivr.net/chartist.js/latest/chartist.min.js"></script>
<link href="//cdn.jsdelivr.net/chartist.js/latest/chartist.min.css" rel="stylesheet" type="text/css" />

<script type="text/javascript" src="<%=request.getContextPath()%>/js/modules/statistics.js"></script>
--%>

<script src="https://code.highcharts.com/highcharts.js"></script>
<script src="https://code.highcharts.com/modules/exporting.js"></script>

<script>

    Liferay.on( 'allPortletsReady', function() {
        let projectClearingSummaryList = [];
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

        <%--
        projectClearingSummaryList.push({
            "Project Name": "${project.name}",
            "Under Clearing By Project Team": ${project.releaseClearingStateSummary.underClearingByProjectTeam},
            "Under Clearing": ${project.releaseClearingStateSummary.underClearing},
            "New Release": ${project.releaseClearingStateSummary.newRelease},
            "Report Available": ${project.releaseClearingStateSummary.reportAvailable},
            "Approved": ${project.releaseClearingStateSummary.approved}
        });

        list.push({
            projectName: "${project.name}",
            underClearingByProjectTeam: ${project.releaseClearingStateSummary.underClearingByProjectTeam},
            underClearing: ${project.releaseClearingStateSummary.underClearing},
            newRelease: ${project.releaseClearingStateSummary.newRelease},
            reportAvailable: ${project.releaseClearingStateSummary.reportAvailable},
            approved: ${project.releaseClearingStateSummary.approved}
        });
        --%>

        projectInfo.push({
            name: "${project.name}",
            id: "${project.id}"
        });
        series[0].data.push(${project.releaseClearingStateSummary.approved});
        series[1].data.push(${project.releaseClearingStateSummary.reportAvailable});
        series[2].data.push(${project.releaseClearingStateSummary.newRelease});
        series[3].data.push(${project.releaseClearingStateSummary.underClearing});
        series[4].data.push(${project.releaseClearingStateSummary.underClearingByProjectTeam});
        </core_rt:forEach>
        <%--
        drawTestChart( projectClearingSummaryList );
        drawBarChart( projectClearingSummaryList );
        --%>
        drawChart(projectInfo, series);
    });

<%--
    function drawBarChart( data ) {
        let chartData = data.slice( 1 ).slice( -15 );
        YUI().use('charts', function (Y) {
            var tooltipDef = {
                styles: {
                    backgroundColor: "#333",
                    color: "#eee",
                    borderColor: "#fff",
                    textAlign: "center"
                },
                markerLabelFunction: function( categoryItem, valueItem, itemIndex, series, seriesIndex ) {

                    var tooltip = document.createElement("div"),
                        projectText = document.createElement("span"),
                        releaseText = document.createElement("div");
                    releaseText.style.marginTop = "10px";
                    releaseText.style.fontSize = "12px";
                    projectText.style.fontSize = "16px";
                    projectText.style.textDecoration = "underline";
                    projectText.appendChild(
                        document.createTextNode(
                            categoryItem
                                .axis
                                .get("labelFunction")
                                .apply(this, [categoryItem.value, categoryItem.axis.get("labelFormat")])
                        )
                    );
                    releaseText.appendChild(
                        document.createTextNode(valueItem.displayName + ": " +
                            valueItem
                                .axis
                                .get("labelFunction")
                                .apply(this, [valueItem.value, valueItem.axis.get("labelFormat")])
                        )
                    );
                    tooltip.appendChild(projectText);
                    tooltip.appendChild(document.createElement("br"));
                    tooltip.appendChild(releaseText);
                    return tooltip;
                }
            };
            var chart = new Y.Chart({
                render: "#licensedebtchart",
                dataProvider: chartData,
                categoryKey:"Project Name",
                stacked: true,
                type:"bar",
                styles:{
                    axes:{
                        values:{
                            label:{
                                fontSize:"11px",
                                color:"#000"
                            }
                        },
                        "Project Name":{
                            label:{
                                rotation:-25,
                                fontSize:"11px",
                                color: "#000"
                            }
                        }
                    },
                    series:{
                        "Approved":{
                            fill:{
                                color:"#caebf2"
                            }
                        },
                        "Report Available":{
                            fill:{
                                color:"#a9a9a9"
                            }
                        },
                        "New Release":{
                            fill:{
                                color:"#ff3b3f"
                            }
                        },
                        "Under Clearing":{
                            fill:{
                                color:"#efefef"
                            }
                        },
                        "Under Clearing By Project Team":{
                            fill:{
                                color:"#efefef"
                            }
                        }
                    }
                },
                verticalGridlines: {
                    styles: {
                        line: {
                            color: "#c4c3b6"
                        }
                    }
                },
                interactionType:"planar",
                tooltip: tooltipDef
            });

            Y.on("click", function(e) {
                var projectName = Y.one("#licensedebtchart") ;
                console.log('projectName', projectName);
            }, "#licensedebtchart");

        });
    }
--%>

    function drawChart(projectInfo, series) {

        <%--
        var labelss = [];
        var ser1 = [];
        var ser2 = [];
        var ser3 = [];
        var ser4 = [];
        var ser5 = [];

        input.forEach( function(e) {
            labelss.push(e.projectName);
            ser1.push(e.underClearingByProjectTeam);
            ser2.push(e.underClearing);
            ser3.push(e.newRelease);
            ser4.push(e.reportAvailable);
            ser5.push(e.approved);
        });

        --%>

        console.log(projectInfo);
        console.log(series);

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
                    pointWidth: 30,
                    events: {
                        click: function (event) {
                            console.log(event.point.category);
                            console.log('this', this);
                            console.log('event', event);
                            console.log('projectInfo', projectInfo);

                            projectInfo.forEach( e => {
                                if (e.name ===  event.point.category) {
                                    console.log('e.name', e.name);
                                    console.log('e.id', e.id);
                                    <%--
                                    loadDetails(e.id);
                                    --%>
                                }
                            });
                        }
                    }
                }
            },
            series: series
        });
    }

<%--
    function drawTestChart(input) {

        console.log(input);

        var labelss = [];
        var ser1 = [];
        var ser2 = [];
        var ser3 = [];
        var ser4 = [];
        var ser5 = [];

        input.forEach( function(e) {
            labelss.push(e.projectName);
            ser1.push(e.underClearingByProjectTeam);
            ser2.push(e.underClearing);
            ser3.push(e.newRelease);
            ser4.push(e.reportAvailable);
            ser5.push(e.approved);
        });

        console.log('labelss', labelss);
        console.log('ser1', ser1);
        console.log('ser2', ser2);
        console.log('ser3', ser3);
        console.log('ser4', ser4);
        console.log('ser5', ser5);


        new Chartist.Bar('#licensedebtchart', {
            labels: labelss,
            series: [
                ser1, ser2, ser3, ser4, ser5
            ]
        }, {
            stackBars: true,
            horizontalBars: true,
        }).on('draw', function(data) {
            if(data.type === 'bar') {
                data.element.attr({
                style: 'stroke-width: 30px'
                });
            }
        });

    }
--%>

<%--
    function drawTestChart(input) {

        console.log(input);

        var labels, ser1, ser2, ser3, ser4, ser5 = [];
    /*
        "Under Clearing By Project Team": ${project.releaseClearingStateSummary.underClearingByProjectTeam},
        "Under Clearing": ${project.releaseClearingStateSummary.underClearing},
        "New Release": ${project.releaseClearingStateSummary.newRelease},
        "Report Available": ${project.releaseClearingStateSummary.reportAvailable},
        "Approved": ${project.releaseClearingStateSummary.approved}
    */
        input.forEach(e => {
            labels.push(e."Project Name");
            ser1.push(e."Under Clearing By Project Team");
            ser1.push(e."Under Clearing");
            ser1.push(e."New Release");
            ser1.push(e."Report Available");
            ser1.push(e."Approved");
        });

        new Chartist.Bar('#licensedebtchart', {
            labels: labels,
            series: [
                ser1, ser2, ser3, ser4, ser5
            ]
        }, {
            stackBars: true,
            horizontalBars: true,
        }).on('draw', function(data) {
            if(data.type === 'bar') {
                data.element.attr({
                style: 'stroke-width: 30px'
                });
            }
        });


        function getLabelKey() {
            return "Project Name";
        }
    }
--%>

</script>