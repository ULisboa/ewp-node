package pt.ulisboa.ewp.node.config.domain;

import jakarta.persistence.EntityManagerFactory;
import java.util.Properties;
import javax.sql.DataSource;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import pt.ulisboa.ewp.node.domain.utils.DatabaseProperties;

@Configuration
@EnableTransactionManagement
public class DomainConfig {

  private DatabaseProperties databaseProperties;

  public DomainConfig(DatabaseProperties databaseProperties) {
    this.databaseProperties = databaseProperties;
  }

  @Bean
  public LocalSessionFactoryBean sessionFactory() {
    LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
    sessionFactory.setDataSource(dataSource());
    sessionFactory.setPackagesToScan("pt.ulisboa.ewp.node.domain.entity");
    sessionFactory.setHibernateProperties(hibernateProperties());

    return sessionFactory;
  }

  @Bean
  public DataSource dataSource() {
    BasicDataSource dataSource = new BasicDataSource();
    dataSource.setDriverClassName(databaseProperties.getDriverClassName());
    dataSource.setUrl(databaseProperties.getUrl());
    dataSource.setUsername(databaseProperties.getUsername());
    dataSource.setPassword(databaseProperties.getPassword());
    dataSource.setValidationQuery("SELECT 1");

    return dataSource;
  }

  @Bean
  public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
    JpaTransactionManager transactionManager = new JpaTransactionManager();
    transactionManager.setEntityManagerFactory(entityManagerFactory);
    return transactionManager;
  }

  private Properties hibernateProperties() {
    Properties hibernateProperties = new Properties();
    hibernateProperties.setProperty(
        "hibernate.hbm2ddl.auto", databaseProperties.getHibernate().getHbm2ddlAuto());
    hibernateProperties.setProperty(
        "hibernate.dialect", databaseProperties.getHibernate().getDialect());
    hibernateProperties.setProperty(
        "hibernate.jdbc.time_zone", databaseProperties.getHibernate().getTimeZone());

    return hibernateProperties;
  }
}
