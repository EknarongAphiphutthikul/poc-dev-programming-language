package th.eknarong.aph.poc.pocjpaormspringboot.config

import com.zaxxer.hikari.HikariDataSource
import org.springframework.boot.actuate.health.Health
import org.springframework.boot.actuate.health.HealthIndicator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
import javax.sql.DataSource

@Configuration
class HikariConfig {
    
    @Bean
    fun hikariHealthIndicator(dataSource: DataSource): HikariHealthIndicator {
        return HikariHealthIndicator(dataSource)
    }
}

@Component
class HikariHealthIndicator(private val dataSource: DataSource) : HealthIndicator {
    
    override fun health(): Health {
        return try {
            if (dataSource is HikariDataSource) {
                val poolStats = dataSource.hikariPoolMXBean
                
                Health.up()
                    .withDetail("pool.active", poolStats.activeConnections)
                    .withDetail("pool.idle", poolStats.idleConnections)
                    .withDetail("pool.total", poolStats.totalConnections)
                    .withDetail("pool.awaiting", poolStats.threadsAwaitingConnection)
                    .withDetail("pool.max", dataSource.maximumPoolSize)
                    .withDetail("pool.min", dataSource.minimumIdle)
                    .withDetail("pool.name", dataSource.poolName)
                    .build()
            } else {
                Health.up()
                    .withDetail("type", dataSource.javaClass.simpleName)
                    .build()
            }
        } catch (e: Exception) {
            Health.down()
                .withDetail("error", e.message)
                .build()
        }
    }
}