package com.blexr.config;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jmx.support.RegistrationPolicy;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.blexr.dao.impl.GameBrandDaoImpl;
import com.blexr.dao.impl.GameDaoImpl;
import com.blexr.dao.impl.GameJurisdictionDaoImpl;
import com.blexr.dao.impl.GamePlatformDaoImpl;
import com.blexr.dao.impl.GameReelDaoImpl;
import com.blexr.dao.impl.GameTypeDaoImpl;
import com.blexr.dao.impl.ImageDaoImpl;
import com.blexr.dao.impl.JurisdictionDaoImpl;
import com.blexr.util.Crawler;

@org.springframework.context.annotation.Configuration
@EnableWebMvc
@EnableAsync
@EnableMBeanExport(registration = RegistrationPolicy.IGNORE_EXISTING)
public class Configuration {

    private static final Logger logger = LoggerFactory.getLogger(Configuration.class);

    @Value("${connection.name}")
    private String connectionName;

    @Value("${connection.url}")
    private String connectionUrl;

    @Value("${connection.driver}")
    private String connectionDriver;

    @Value("${connection.username}")
    private String connectionUsername;

    @Value("${connection.password}")
    private String connectionPassword;

    @Bean
    public DataSource dataSource() {
	BasicDataSource dataSource = new BasicDataSource();
	logger.info("conDriver=" + connectionDriver);
	logger.info("connectionUrl=" + connectionUrl);
	logger.info("connectionUsername=" + connectionUsername);
	logger.info("connectionPassword=" + connectionPassword);
	dataSource.setDriverClassName(connectionDriver);
	dataSource.setUrl(connectionUrl);
	dataSource.setUsername(connectionUsername);
	dataSource.setPassword(connectionPassword);
	return dataSource;
    }
    
    @Bean
    public JdbcTemplate jdbcTemplate() {
      JdbcTemplate jdbcTemplate = new JdbcTemplate();
      jdbcTemplate.setDataSource(dataSource());
      return jdbcTemplate;
    }
    
    @Bean
    public Crawler myCrawler() {
	return new Crawler();
    }
    
    @Bean
    public GameDaoImpl gameDaoImpl() {
	return new GameDaoImpl();
    }
    
    @Bean
    public ImageDaoImpl imageDaoImpl() {
	return new ImageDaoImpl();
    }
    
    @Bean
    public JurisdictionDaoImpl jurisdictionDaoImpl() {
	return new JurisdictionDaoImpl();
    }
    
    @Bean
    public GameJurisdictionDaoImpl gameJurisdictionDaoImpl() {
	return new GameJurisdictionDaoImpl();
    }
    
    @Bean
    public GameBrandDaoImpl gameBrandDaoImpl() {
	return new GameBrandDaoImpl();
    }
    
    @Bean
    public GamePlatformDaoImpl gamePlatformDaoImpl() {
	return new GamePlatformDaoImpl();
    }
    
    @Bean
    public GameReelDaoImpl gameReelDaoImpl() {
	return new GameReelDaoImpl();
    }
    
    @Bean
    public GameTypeDaoImpl gameTypeDaoImpl() {
	return new GameTypeDaoImpl();
    }    
}
