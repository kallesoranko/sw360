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
    <div id="licensedebtchart"></div>
</div>

<script>

    Liferay.on( 'allPortletsReady', function() {
        let projectClearingSummaryList = [];
        <core_rt:forEach items="${projects}" var="project">
        projectClearingSummaryList.push({
            "Project Name": "${project.name}",
            "Under Clearing By Project Team": ${project.releaseClearingStateSummary.underClearingByProjectTeam},
            "Under Clearing": ${project.releaseClearingStateSummary.underClearing},
            "New Release": ${project.releaseClearingStateSummary.newRelease},
            "Report Available": ${project.releaseClearingStateSummary.reportAvailable},
            "Approved": ${project.releaseClearingStateSummary.approved}
        });
        </core_rt:forEach>
        drawBarChart( projectClearingSummaryList );
    });

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
                    releaseText.style.marginTop = "5px";
                    projectText.style.fontSize = "12px";
                    releaseText.style.fontSize = "15px";
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
                tooltip: tooltipDef
            });
        });
    }

</script>