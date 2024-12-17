package com.devkobe24.kobe_bulletin_board.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
public class MySQLConfig {

	@Value("jdbc:mysql://localhost:3306/board")
	private String url;

	@Value("root")
	private String username;

	@Value("@@SkyAchieve910424")
	private String password;

	@Value("com.mysql.cj.jdbc.Driver")
	private String driverClassName;

	@Bean
	public DataSourceTransactionManager transactionManager(DataSource dataSource) {
		return new DataSourceTransactionManager(dataSource);
	}

	@Bean
	public TransactionTemplate transactionTemplate(PlatformTransactionManager transactionManager) {
		return new TransactionTemplate(transactionManager);
	}

	@Bean(name = "createUserTransactionManager")
	public PlatformTransactionManager createUserTransactionManager(DataSource dataSource) {
		DataSourceTransactionManager manager = new DataSourceTransactionManager(dataSource);
		return manager;
	}

	@Bean(name = "loginTransactionManager")
	public PlatformTransactionManager loginTransactionManager(DataSource dataSource) {
		DataSourceTransactionManager manager = new DataSourceTransactionManager(dataSource);
		return manager;
	}

	@Bean(name = "createPostTransactionManager")
	public PlatformTransactionManager createPostTransactionManager(DataSource dataSource) {
		DataSourceTransactionManager manager = new DataSourceTransactionManager(dataSource);
		return manager;
	}

	@Bean(name = "readPostTransactionManager")
	public PlatformTransactionManager readPostTransactionManager(DataSource dataSource) {
		DataSourceTransactionManager manager = new DataSourceTransactionManager(dataSource);
		return manager;
	}
}
