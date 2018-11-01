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
    });

    function drawBarChart( data ) {
        let chartData = data.slice( 1 ).slice( -10 );

        console.log('chartData', chartData);

        YUI().use('charts', function (Y) {

             var styleDef = {
                axes:{
                    "Approved":{
                        label:{
                            rotation:-45,
                            color:"#000000"
                        }
                    }
                },
                series:{
                    "Approved":{
                        fill:{
                            color:"#ddd"
                        }
                    },
                    "Report Available":{
                        fill:{
                            color:"#ddd"
                        }
                    },
                    "New Release":{
                        fill:{
                            color:"#ddd"
                        }
                    },
                    "Under Clearing":{
                        fill:{
                            color:"#ddd"
                        }
                    },
                    "Under Clearing By Project Team":{
                        fill:{
                            color:"#ddd"
                        }
                    }
                }

                };

            var chart = new Y.Chart({
                render: "#licensedebtchart",
                dataProvider: chartData2,
                categoryKey:"Project Name",
                stacked: true,
                type:"bar",
                styles:styleDef,
                verticalGridlines:true
            });
        });
    }

</script>