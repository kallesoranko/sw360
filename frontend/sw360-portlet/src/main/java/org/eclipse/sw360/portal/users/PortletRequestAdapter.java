/*
 * Copyright Siemens AG, 2018. Part of the SW360 Portal Project.
 *
 * SPDX-License-Identifier: EPL-1.0
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.sw360.portal.users;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.ServiceContextFactory;

import javax.portlet.PortletRequest;
import java.util.Optional;
import java.util.function.Consumer;

class PortletRequestAdapter implements RequestAdapter {

    private PortletRequest request;

    PortletRequestAdapter(PortletRequest request) {
        this.request = request;
    }

    @Override
    public long getCompanyId() {
        return UserUtils.getCompanyId(request);
    }

    @Override
    public Consumer<String> getErrorMessagesConsumer() {
        return msg -> SessionMessages.add(request, "request_processed", msg);
    }

    @Override
    public Optional<ServiceContext> getServiceContext() {
        try {
            return Optional.of(ServiceContextFactory.getInstance(request));
        } catch (PortalException | SystemException e ) {
            return Optional.empty();
        }
    }
}
