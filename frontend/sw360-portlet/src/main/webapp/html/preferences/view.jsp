<%--
  ~ Copyright Siemens AG, 2017-2018. Part of the SW360 Portal Project.
  ~
  ~ SPDX-License-Identifier: EPL-1.0
  ~
  ~ All rights reserved. This program and the accompanying materials
  ~ are made available under the terms of the Eclipse Public License v1.0
  ~ which accompanies this distribution, and is available at
  ~ http://www.eclipse.org/legal/epl-v10.html
--%>
<%@ page import="org.eclipse.sw360.datahandler.thrift.users.User" %>

<%@include file="/html/init.jsp" %>
<%-- the following is needed by liferay to display error messages--%>
<%@include file="/html/utils/includes/errorKeyToMessage.jspf"%>
<portlet:defineObjects/>
<liferay-theme:defineObjects/>

<portlet:actionURL var="generateTokenURL" name="createToken"></portlet:actionURL>
<portlet:actionURL var="saveUserPreferencesURL" name="savePreferences"></portlet:actionURL>

<jsp:useBean id="sw360User" type="org.eclipse.sw360.datahandler.thrift.users.User" scope="request"/>
<jsp:useBean id="eventsConfig" type="java.util.Map<java.lang.String, java.util.List<java.util.Map.Entry<java.lang.String, java.lang.String>>>" scope="request"/>
<jsp:useBean id="accessToken" class="org.eclipse.sw360.datahandler.thrift.RestApiToken" scope="request"/>

<link rel="stylesheet" href="<%=request.getContextPath()%>/css/sw360.css">

<div id="header"></div>
<p class="pageHeader"><span class="pageHeaderBigSpan">User Preferences</span></p>
<div id="content">
    <table class="table info_table" id="readOnlyUserData">
        <thead>
            <tr>
                <th colspan="2">SW360 User</th>
            </tr>
        </thead>
        <tr>
            <td>Name:</td>
            <td><sw360:out value="${sw360User.fullname}"/></td>
        </tr>
        <tr>
            <td>E-mail:</td>
            <td><sw360:DisplayUserEmail email="${sw360User.email}" bare="true"/></td>
        </tr>
        <tr>
            <td>Group:</td>
            <td><sw360:out value="${sw360User.department}"/></td>
        </tr>
        <tr>
            <td>External Id:</td>
            <td><sw360:out value="${sw360User.externalid}"/></td>
        </tr>
        <tr>
            <td>Role:</td>
            <td><sw360:DisplayEnum value="${sw360User.userGroup}"/></td>
        </tr>
    </table>
    <form action="<%=generateTokenURL%>" method="post">
        <table class="table info_table" id="restInfoTable">
        <thead>
        <tr>
            <th colspan="2">REST API Token</th>
        </tr>
        </thead>
        <tr>
            <td>Token:
                <img class="infopic" src="<%=request.getContextPath()%>/images/ic_info.png"
                     title="Authorization http-header (Authorization: bearer <token>)"/>
            </td>
            <td><sw360:out value="${accessToken.value}"/></td>
        </tr>
        <tr>
            <td>Authorities:</td>
            <td><sw360:out value="${accessToken.authorities}"/></td>
        </tr>
        <tr>
            <td>Scope:</td>
            <td><sw360:out value="${accessToken.scope}"/></td>
        </tr>
        <tr>
            <td>Expiration:</td>
            <td><sw360:out value="${accessToken.expiration}"/></td>
        </tr>
    </table>
        <input type="submit" class="addButton" value="Generate Token">
    </form>
    <br/>
    <form action="<%=saveUserPreferencesURL%>" id="preferences_form" method="post">
        <table class="table info_table" id="readOnlyUserData-rest">
            <thead>
            <tr>
                <th colspan="1">E-Mail Notification Preferences</th>
            </tr>
            </thead>
            <tr>
                <td>
                    <p>
                        <input type="checkbox" name="<portlet:namespace/><%=User._Fields.WANTS_MAIL_NOTIFICATION%>" id="wants_mail_notification"
                                <core_rt:if test="${sw360User.wantsMailNotification == 'true'}"> checked="checked" </core_rt:if>/>
                        <label class="checkboxlabel inlinelabel" for="wants_mail_notification">Enable E-Mail Notifications</label>
                    </p>
                </td>
            </tr>
            <core_rt:forEach items="${eventsConfig}" var="config">
                <tr class="notifiable_events">
                    <td>
                        <h6>Receive notifications for changes in ${config.key} where your role is:</h6>
                        <core_rt:forEach items="${config.value}" var="fieldEntry">
                            <input type="checkbox" class="indented"
                                   name="<portlet:namespace/><%=User._Fields.NOTIFICATION_PREFERENCES%>${config.key}${fieldEntry.key}" id="${config.key}${fieldEntry.key}"
                                    <core_rt:if test="${'true' == sw360User.notificationPreferences[config.key.concat(fieldEntry.key)]}"> checked="checked" </core_rt:if>/>
                            <label class="checkboxlabel inlinelabel" for="${config.key}${fieldEntry.key}">${fieldEntry.value}</label><br>
                        </core_rt:forEach>
                    </td>
                </tr>
            </core_rt:forEach>
        </table>
        <input type="submit" class="addButton" value="Save">
    </form>
</div>

<%--for javascript library loading --%>
<%@ include file="/html/utils/includes/requirejs.jspf" %>
<script>
    AUI().use('liferay-portlet-url', function () {
        var PortletURL = Liferay.PortletURL;

        require(['jquery'], function($) {

            var $wantsMailNotification = $('#wants_mail_notification');
            setDisabledStateOfPreferenceCheckboxes(!$wantsMailNotification.is(":checked"));

            // register event handlers
            $wantsMailNotification.on('change', function() {
                setDisabledStateOfPreferenceCheckboxes(!this.checked);
            });

            function setDisabledStateOfPreferenceCheckboxes(disabled){
                $(".notifiable_events").find(":checkbox").each(function () {
                    this.disabled = disabled;
                })
            }
        });
    });
</script>
