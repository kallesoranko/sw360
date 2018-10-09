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
               <col style="width: 50%;"/>
               <col style="width: 50%;"/>
         </colgroup>
    </table>

    <div id="myLicenseDebtDiv">
        <div class="clearing_detail_controls">
            <aui:select label="Select Project:" id="options" name="selectField1" required="true" showEmptyOption="false" inlineLabel="left">
                <core_rt:forEach items="${projects}" var="project">
                    <aui:option value="${project.id}">"${project.name}"</aui:option>
                </core_rt:forEach>
            </aui:select>
            <span class="stackedlabel">
                <%-- do we use aui:button or input? --%>
                <%--<input type="button" id="edit" value="Select" class="addButton">--%>

                <aui:button value="Get Clearing Details" id="btnSubmit" cssClass="btn btn-primary"/>
            </span>
        </div>

<%--
    <div class="btn-group" role="group" aria-label="Button group with nested dropdown">
        <button type="button" class="btn btn-secondary">1</button>
        <button type="button" class="btn btn-secondary">2</button>

        <div class="btn-group" role="group">
            <button id="btnGroupDrop1" type="button" class="btn btn-secondary dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                Dropdown
            </button>
            <div class="dropdown-menu" aria-labelledby="btnGroupDrop1">
                <a class="dropdown-item" href="#">Dropdown link</a>
                <a class="dropdown-item" href="#">Dropdown link</a>
            </div>
        </div>
    </div>

    <div class="aui-buttons">
        <button class="aui-button">Button1</button>
        <button class="aui-button">Button2</button>
        <button class="aui-button">Button3</button>
    </div>


    <aui:script use="node, event">

    </aui:script>
--%>
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
    var btn = A.one('#btnSubmit');
    var option = A.one('#<portlet:namespace/>options')
    btn.on('click', function(event){
        updatePieChartWithProjectId(option.val());
    });
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
