<%--
  ~ Copyright (c) Verifa Oy, 2018.
  ~
  ~ This program and the accompanying materials are made
  ~ available under the terms of the Eclipse Public License 2.0
  ~ which is available at https://www.eclipse.org/legal/epl-2.0/
  ~
  ~ SPDX-License-Identifier: EPL-2.0
  --%>

<%@ taglib prefix="min-width" uri="http://liferay.com/tld/aui" %>
<%@ taglib prefix="min-height" uri="http://liferay.com/tld/aui" %>

<%@include file="/html/init.jsp" %>
<%-- the following is needed by liferay to display error messages--%>
<%@include file="/html/utils/includes/errorKeyToMessage.jspf"%>
<%@ page import="com.liferay.portal.kernel.portlet.PortletURLFactoryUtil" %>
<%@ page import="org.eclipse.sw360.portal.common.PortalConstants" %>
<%@ page import="javax.portlet.PortletRequest" %>
<%@ page import="org.eclipse.sw360.datahandler.thrift.projectimport.TokenCredentials" %>
<%@ page import="org.eclipse.sw360.datahandler.thrift.projects.Project" %>
<%@ page import="org.eclipse.sw360.portal.portlets.projectimport.ProjectImportConstants" %>

<portlet:defineObjects/>
<liferay-theme:defineObjects/>
<portlet:resourceURL var="ajaxURL" id="bdhubimport.jsp"></portlet:resourceURL>

<div id="header"></div>
<p class="pageHeader">
    <span class="pageHeaderBigSpan"><liferay-ui:message key="bdhubimport" /></span>
    <span class="pull-right"></span>
</p>

<div id="bdhubimport-source" class="content1">
    <div id="remoteLoginForm">
        <div class='form-group'>
            <label class="control-label textlabel stackedLabel" for="input-dataserver-url"><liferay-ui:message key="rest.server.url" />:</label>
            <div class='controls'>
                <input  class="form-control toplabelledInput"
                        id="input-dataserver-url"
                        name="<portlet:namespace/><%=TokenCredentials._Fields.SERVER_URL%>"
                        style="min-width:253px; min-height:33px"
                        value="https://black-duck-hub-server.com"
                        required />
            </div>
        </div>
        <div class='form-group'>
            <label class="control-label textlabel stackedLabel" for="input-dataserver-token"><liferay-ui:message key="bdhub.api.token" />:</label>
            <div class='controls'>
                <input  class="form-control toplabelledInput"
                        id="input-dataserver-token"
                        name="<portlet:namespace/><%=TokenCredentials._Fields.TOKEN%>"
                        style="min-width:243px; min-height:28px"
                        type="password"
                        required />
            </div>
        </div>
    </div>
    <input  type="button"
            class="btn btn-primary"
            onclick="getAllProjects()"
            value="<liferay-ui:message key="get.projects" />"/>
</div>

<div id="bdhubimport-data" class="content2">
    <div id="importProject" class="bdhubimport_content">
        <span><h4><liferay-ui:message key="select.product" /></h4></span>
        <form>
            <table  id="projectTable" class="display bdhubimport_table">
                <tfoot>
                    <tr>
                        <th colspan="1"></th>
                    </tr>
                </tfoot>
            </table>
        </form>
    </div>
    <div id="importProjectVersion" class="bdhubimport_content">
        <span><h4><liferay-ui:message key="select.projects" /></h4></span>
        <form>
            <table  id="projectVersionTable" class="display bdhubimport_table">
                <tfoot>
                    <tr>
                        <th colspan="1"></th>
                    </tr>
                </tfoot>
            </table>
        </form>
        <input  type="button"
                value="<liferay-ui:message key="import" />"
                class="btn btn-primary"
                style="text-align: center;"
                onclick="showImportPopup()"/>
    </div>
</div>


<script>
    let productTable;
    let projectTable;
    let PortletURL;
    let bearerToken;

    AUI().use('liferay-portlet-url', function (A) {
        PortletURL = Liferay.PortletURL;
    });

    AUI().use(
        'aui-form-validator',
        function(Y) {
            new Y.FormValidator(
                {
                    boundingBox: '#remoteLoginForm'
                }
            );
        }
    );

    function getAllProjects() {
        let apiToken = document.getElementById("input-dataserver-token").value;
        if (!token) {
            $.alert({
                title: "<liferay-ui:message key="can.not.get.products" />",
                content: '<liferay-ui:message key="please.enter.organization.api.key.and.user.key" />',
                type: 'orange'
            });
            return false;
        }

        $.ajax({
            method: "POST",
            traditional: true,
            accepts: "application/vnd.blackducksoftware.user-4+json",
            contentType: "application/json",
            url: document.getElementById("input-dataserver-url").value + "/api/tokens/authenticate",
            cache: false,
            headers: {
                "Authorization: token " + apiToken
            },
            success: function( json ) {
                if ((json.errorCode == 1002 && !!token) || !!json.errorCode) {
                    $.alert({
                        title: "<liferay-ui:message key="access.denied" />",
                        content: json.errorMessage,
                        type: 'red'
                    });
                    return false;
                }
                bearerToken = json.bearerToken
                createProjectTable();
            }
        });

        let data = new Object();
        data["<portlet:namespace/><%=ProjectImportConstants.USER_ACTION__IMPORT%>"] = "<%=ProjectImportConstants.USER_ACTION__NEW_IMPORT_SOURCE%>";
        data["<portlet:namespace/><%=ProjectImportConstants.SERVER_URL%>"] = document.getElementById("input-dataserver-url").value;
        data["<portlet:namespace/><%=ProjectImportConstants.API_TOKEN%>"] = document.getElementById("input-dataserver-token").value;
        data["<portlet:namespace/><%=ProjectImportConstants.BEARER_TOKEN%>"] = bearerToken;

        $.ajax({
            url: '<%=ajaxURL%>',
            type: 'POST',
            cache: false,
            dataType: 'json',
            data: data
        });
    }

    function createProjectTable() {
        var project_list = function () {
            var tmp = null;
            $.ajax({
                method: "POST",
                traditional: true,
                accepts: "application/vnd.blackducksoftware.project-detail-5+json",
                contentType: "application/json",
                url: document.getElementById("input-dataserver-url").value + "/api/projects",
                cache: false,
                headers: {
                    Authorization: "Bearer " + bearerToken
                },
                success: function( json ) {
                    if ((json.errorCode == 1002 && !!bearerToken) || !!json.errorCode) {
                        $.alert({
                            title: "<liferay-ui:message key="access.denied" />",
                            content: json.errorMessage,
                            type: 'red'
                        });
                        return false;
                    }
                    tmp = json.items
                }
             });
             return tmp;
         }();

        project_list.forEach( function(e) {
            let token =  e.productToken;
            inputData.push({
                "DT_RowId": e._meta.href,
                "0": e.name
            });
        });

        productTable = $( '#productTable' ).DataTable({
            "autoWidth": false,
            "columns": [
                {"sTitle": "<liferay-ui:message key="project.name" />"}
            ],
            "data": inputData,
            "destroy": true,
            "info": false,
            "paging": false,
            "scrollCollapse": true,
            "scrollY": '45vh',
            "searching": false,
            "select": 'single'
        });
    }

    $('#projectTable').on( 'click', 'tr', function () {
        let tr = $(this).closest('tr');
        let row = projectTable.row( tr );

        if ( $(this).hasClass('selected') ) {
            $(this).removeClass('selected');
        } else {
            $(this).addClass('selected');
            getAllProjectVersions(projectTable.row(this).id());
        }
    } );

    function getAllProjectVersions(projectHref) {
        $.ajax({
            method: "POST",
            traditional: true,
            accepts: "application/vnd.blackducksoftware.project-detail-5+json",
            contentType: "application/vnd.blackducksoftware.project-detail-5+json",
            url: projectHref + "/versions",
            cache: false,
            headers: {
                Authorization: "Bearer " + bearerToken
            },
            success: function( json ) {
                if (json.errorCode == 1003 || !!json.errorCode) {
                    $.alert({
                        title: "<liferay-ui:message key="access.denied" />",
                        content: json.errorMessage,
                        type: 'red'
                    });
                    return false;
                }
                createProjectVersionsTable( json.items );
            }
        });
    }

    function createProjectVersionsTable(projectVersions) {
        let inputData = [];
        json.forEach( function(e) {
            inputData.push({
                "DT_RowId": e._meta.href,
                "0": e.versionName
            });
        });

        projectVersionTable = $( '#projectVersionTable' ).DataTable({
            "autoWidth": false,
            "columns":  [
                { "sTitle": "<liferay-ui:message key="project.name" />"}
            ],
            "data": inputData,
            "destroy": true,
            "info": false,
            "paging": false,
            "scrollCollapse": true,
            "scrollY": '45vh',
            "searching": false,
            "select": 'multi'
        });
    }

    function showImportPopup() {
        let bodyContent = "<ul>";
        let selectedProjectVersions = [];

        projectTable.rows('.selected').data().each( function(e) {
            selectedProjectVersions.push({
                projectVersionName: e["0"],
                projectVersionHref: e["DT_RowId"]
            });
            bodyContent += "<li>" + e["0"] + "</li>";
        });

        bodyContent += "</ul>";

        $.confirm({
            title: '<liferay-ui:message key="the.following.projects.will.be.imported" />',
            content: bodyContent,
            type: 'blue',
            buttons: {
                import: {
                    btnClass: 'btn-green',
                    action: function() {
                        importProjectsData(selectedProjectVersions);
                    }
                },
                cancel: {
                    btnClass: 'btn-red',
                    action: function () {
                        //close
                    }
                },
            }
        });
    }

    function importProjectsData(selectedProjectVersions) {
        $.confirm({
            title: "<liferay-ui:message key="import" />",
            content: function () {
                let self = this;
                return $.ajax({
                    url: '<%=ajaxURL%>',
                    type: 'POST',
                    cache: false,
                    dataType: 'json',
                    data: {
                        "<portlet:namespace/>checked":
                            selectedProjectVersions.map(function (o) { return o["projectVersionHref"]; }),
                        "<portlet:namespace/><%=ProjectImportConstants.USER_ACTION__IMPORT%>":
                            '<%=ProjectImportConstants.USER_ACTION__IMPORT_DATA%>'
                    }
                }).done(function (response) {
                    self.setTitle("Import result");
                    switch(response.<%=ProjectImportConstants.RESPONSE__STATUS%>) {
                        case '<%=ProjectImportConstants.RESPONSE__SUCCESS%>':
                            let bodyContent1 = "<ul>";
                            let successfulIdsList = response.<%=ProjectImportConstants.RESPONSE__SUCCESSFUL_IDS%>;
                            $.each(successfulIdsList, function (key, value) {
                                bodyContent1 += "<li>" + value + "</li>";
                            });
                            bodyContent1 += "</ul>";
                            self.setContent('Projects imported successfully.' + bodyContent1);
                            break;
                        case '<%=ProjectImportConstants.RESPONSE__FAILURE%>':
                            let bodyContent2 = "<ul>";
                            let failedIdsList = response.<%=ProjectImportConstants.RESPONSE__FAILED_IDS%>;
                            $.each(failedIdsList, function (key, value) {
                                bodyContent2 += "<li>" + value + "</li>";
                            });
                            bodyContent2 += "</ul>";
                            self.setContent('Some projects failed to import:' + bodyContent2);
                            break;
                        case '<%=ProjectImportConstants.RESPONSE__GENERAL_FAILURE%>':
                            flashErrorMessage('Could not import the projects.');
                            self.setContent('<liferay-ui:message key="import.failed" />');
                            break;
                        default:
                    }
                }).fail(function(){
                    flashErrorMessage('<liferay-ui:message key="could.not.import.the.projects" />');
                    self.close();
                });
            }
        });
    }

    function makeProjectUrl(projectId, page) {
        let portletURL = PortletURL.createURL('<%= PortletURLFactoryUtil.create(request, portletDisplay.getId(), themeDisplay.getPlid(), PortletRequest.RENDER_PHASE) %>')
                .setParameter('<%=PortalConstants.PAGENAME%>', page)
                .setParameter('<%=PortalConstants.PROJECT_ID%>', projectId);
        return portletURL.toString();
    }

</script>

<%@include file="/html/utils/includes/modal.jspf" %>
