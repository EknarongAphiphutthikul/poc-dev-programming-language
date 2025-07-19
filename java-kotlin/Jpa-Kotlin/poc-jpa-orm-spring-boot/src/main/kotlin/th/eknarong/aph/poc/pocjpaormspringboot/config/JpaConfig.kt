package th.eknarong.aph.poc.pocjpaormspringboot.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement
import jakarta.persistence.EntityManagerFactory
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer
import org.springframework.context.annotation.Profile

@Configuration
@EnableTransactionManagement
class JpaConfig {
    
    @Bean
    fun transactionManager(entityManagerFactory: EntityManagerFactory): PlatformTransactionManager {
        val transactionManager = JpaTransactionManager()
        transactionManager.entityManagerFactory = entityManagerFactory
        return transactionManager
    }
    
    @Bean
    @Profile("!test")  // Don't use in test environment
    fun hibernatePropertiesCustomizer(sqlLoggingInterceptor: SqlLoggingInterceptor): HibernatePropertiesCustomizer {
        return HibernatePropertiesCustomizer { hibernateProperties ->
            hibernateProperties["hibernate.session_factory.interceptor"] = sqlLoggingInterceptor
        }
    }
}