package com.tcs.unison.config;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.stereotype.Component;

import com.tcs.unison.security.APIContext;

@Component
public class TenantRoutingDataSource extends AbstractRoutingDataSource {

	@Autowired
	private MultiTenantConfig multiTenantConfig;

	@Override
	protected Object determineCurrentLookupKey() {
		if (APIContext.get() != null) {
			String tenantId = APIContext.get().getTenantId();
			if (StringUtils.isNotBlank(tenantId)) {
				if (!multiTenantConfig.hasTenant(tenantId)) {
					DataSource dataSource = multiTenantConfig.createDataSourceForTenant(tenantId);
					this.afterPropertiesSet();
					migrate(dataSource);
				}
				return tenantId;
			}
		}
		return "defaultdb";
	}

	@Override
	public void afterPropertiesSet() {
		this.setTargetDataSources(multiTenantConfig.getAllDataSources());
		super.afterPropertiesSet();
	}

	private static void migrate(DataSource dataSource) {
		Flyway flyway = Flyway.configure().dataSource(dataSource).baselineOnMigrate(true).load();
		flyway.repair();
		flyway.migrate();
	}
}
