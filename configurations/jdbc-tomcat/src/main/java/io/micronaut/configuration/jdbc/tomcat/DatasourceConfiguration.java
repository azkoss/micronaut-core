/*
 * Copyright 2017 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.micronaut.configuration.jdbc.tomcat;

import io.micronaut.context.annotation.Parameter;
import io.micronaut.context.annotation.EachProperty;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import io.micronaut.jdbc.BasicJdbcConfiguration;
import io.micronaut.jdbc.CalculatedSettings;

import javax.annotation.PostConstruct;

/**
 * Allows the configuration of Tomcat JDBC data sources. All properties on
 * {@link PoolProperties} are available to be configured.
 *
 * If the url, driver class, username, or password are missing, sensible defaults
 * will be provided when possible. If no configuration beyond the datasource name
 * is provided, an in memory datastore will be configured based on the available
 * drivers on the classpath.
 *
 * @author James Kleeh
 * @since 1.0
 */
@EachProperty(value = "datasources", primary = "default")
public class DatasourceConfiguration extends PoolProperties implements BasicJdbcConfiguration {

    private CalculatedSettings calculatedSettings;

    public DatasourceConfiguration(@Parameter String name) {
        super();
        this.setName(name);
        this.calculatedSettings = new CalculatedSettings(this);
    }

    @PostConstruct
    void postConstruct() {
        getDriverClassName();
        getUrl();
        getUsername();
        getPassword();
        getValidationQuery();
    }

    @Override
    public String getDriverClassName() {
        return calculatedSettings.getDriverClassName();
    }

    @Override
    public String getConfiguredDriverClassName() {
        return super.getDriverClassName();
    }

    @Override
    public String getUrl() {
        return calculatedSettings.getUrl();
    }

    @Override
    public String getConfiguredUrl() {
        return super.getUrl();
    }

    @Override
    public String getUsername() {
        return calculatedSettings.getUsername();
    }

    @Override
    public String getConfiguredUsername() {
        return super.getUsername();
    }

    @Override
    public String getPassword() {
        return calculatedSettings.getPassword();
    }

    @Override
    public String getConfiguredPassword() {
        return super.getPassword();
    }

    @Override
    public String getValidationQuery() {
        return calculatedSettings.getValidationQuery();
    }

    @Override
    public String getConfiguredValidationQuery() {
        return super.getValidationQuery();
    }

    public String getJndiName() {
        return getDataSourceJNDI();
    }

    public void setJndiName(String jndiName) {
        setDataSourceJNDI(jndiName);
    }
}
