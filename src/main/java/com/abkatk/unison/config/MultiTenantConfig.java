package com.abkatk.unison.config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.support.VaultResponse;

import com.zaxxer.hikari.HikariDataSource;

@Configuration
public class MultiTenantConfig {

	@Autowired
	private VaultTemplate vaultTemplate;
	
	@Autowired
	@Lazy
	private TenantRoutingDataSource routingDataSource;

	private final Map<Object, Object> tenantDataSources = new ConcurrentHashMap<>();

	public boolean hasTenant(String tenantId) {
	    return tenantDataSources.containsKey(tenantId);
	}
	
	public Map<Object, Object> getAllDataSources() {
	    return tenantDataSources;
	}

	@Bean
	@Primary
	DataSource dataSource() {
		// Create default tenant DataSource
		DataSource defaultDataSource = createDataSourceForTenant("defaultdb");
		tenantDataSources.put("defaultdb", defaultDataSource);
		routingDataSource.setTargetDataSources(tenantDataSources);
		routingDataSource.afterPropertiesSet();

		return routingDataSource;
	}
	
	
	@SuppressWarnings("unchecked")
	public DataSource createDataSourceForTenant(String tenantId) {
		VaultResponse response = vaultTemplate.read("secret/data/unison");
		HikariDataSource ds = new HikariDataSource();

		if (response != null) {

			Map<String, Object> data = (Map<String, Object>) response.getData().get("data");
			if (!data.containsKey("dburl") || !data.containsKey("dbusername") || !data.containsKey("dbpassword")) {
				throw new RuntimeException("Incomplete DB config in Vault for tenant: " + tenantId);
			}
			String dbUrl = data.get("dburl") + tenantId
					+ "?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
			ds.setJdbcUrl(dbUrl);
			ds.setUsername((String) data.get("dbusername"));
			ds.setPassword((String) data.get("dbpassword"));
			ds.setDriverClassName("com.mysql.cj.jdbc.Driver");
		} else {
			String dbUrl = "jdbc:mysql://localhost:3306/" + tenantId
					+ "?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
			ds.setJdbcUrl(dbUrl);
			ds.setUsername("root");
			ds.setPassword("mysql");
			ds.setDriverClassName("com.mysql.cj.jdbc.Driver");
		}
		tenantDataSources.put(tenantId, ds);
        ds.setMaximumPoolSize(20);
        ds.setMinimumIdle(0);
		return ds;
	}
}