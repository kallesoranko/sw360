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
            <aui:select label="Select Project:" id="options" name="selectField1" required="true" showEmptyOption="false" inlineLabel="left">
            <core_rt:forEach items="${projects}" var="project">
            <aui:option value="${project.id}">${project.name}</aui:option>
            </core_rt:forEach>
            </aui:select>
            <aui:button value="Get Clearing Details" id="btnSubmit" cssClass="btn btn-primary"/>
        </div>
        <div id="licensedebtchart"></div>
    </div>

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

    <aui:script use="node, event">
    var btn = A.one( '#btnSubmit' )
    var option = A.one( '#<portlet:namespace/>options' )
    btn.on('click', function(event){
        updatePieChartWithProjectId(option.val());
    })
    </aui:script>

    function updatePieChartWithProjectId(projectId) {
        $.ajax({
            url: '<%=ajaxURL%>',
            type: 'POST',
            cache: false,
            dataType: 'json',
            data: {
                "<portlet:namespace/><%=PortalConstants.PROJECT_ID%>" :  projectId
            }
        }).done(function(response) {
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
            drawPieChart(datapoints);
        }).fail(function(response) {
            console.log('ERROR', response);
        });
    }

    function drawPieChart(points) {
        var styleDef = {
                colors: ["#6084d0", "#eeb647", "#6c6b5f", "#d6484f", "#ce9ed1","#B00000","#D8D8D8","#339966","#FF3399","#666633","#336600","#9966FF"]
        };
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
                }],
                styles: styleDef
            });
        });
    }

</script>
