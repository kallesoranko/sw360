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
package org.eclipse.sw360.rest.resourceserver.core;

import org.apache.thrift.TException;
import org.eclipse.sw360.datahandler.thrift.users.User;

import java.util.Map;
import java.util.Set;

public interface AwareOfRestServices<T> {

    Set<T> searchByExternalIds(Map<String, Set<String>> externalIds, User user) throws TException;

    T convertToEmbeddedWithExternalIds(T sw360Object);

}
