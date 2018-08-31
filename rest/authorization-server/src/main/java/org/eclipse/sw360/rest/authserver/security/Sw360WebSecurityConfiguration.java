/*
 * Copyright Siemens AG, 2017-2018. Part of the SW360 Portal Project.
 *
 * SPDX-License-Identifier: EPL-1.0
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.eclipse.sw360.rest.authserver.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class Sw360WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final Sw360AuthenticationProvider sw360AuthenticationProvider;

    public Sw360WebSecurityConfiguration(Sw360AuthenticationProvider sw360AuthenticationProvider) {
        this.sw360AuthenticationProvider = sw360AuthenticationProvider;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder authenticationManagerBuilder) {
        authenticationManagerBuilder.authenticationProvider(this.sw360AuthenticationProvider);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/generateToken").hasIpAddress("127.0.0.1/32")
                .antMatchers(HttpMethod.OPTIONS).permitAll() // some JS frameworks make HTTP OPTIONS requests
                .anyRequest().authenticated()
                .and().httpBasic()
                .and().csrf().disable();
    }
}
