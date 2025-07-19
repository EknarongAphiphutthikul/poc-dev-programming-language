package th.eknarong.aph.poc.backup.controller

import com.zaxxer.hikari.HikariDataSource
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.sql.DataSource

@RestController
@RequestMapping("/api/metrics")
class HikariMetricsController(private val dataSource: DataSource) {
    
    @GetMapping("/hikari/pool-stats")
    fun getHikariPoolStats(): Map<String, Any> {
        return if (dataSource is HikariDataSource) {
            val poolStats = dataSource.hikariPoolMXBean
            
            mapOf(
                "poolName" to dataSource.poolName,
                "activeConnections" to poolStats.activeConnections,
                "idleConnections" to poolStats.idleConnections,
                "totalConnections" to poolStats.totalConnections,
                "threadsAwaitingConnection" to poolStats.threadsAwaitingConnection,
                "maximumPoolSize" to dataSource.maximumPoolSize,
                "minimumIdle" to dataSource.minimumIdle,
                "connectionTimeout" to dataSource.connectionTimeout,
                "idleTimeout" to dataSource.idleTimeout,
                "maxLifetime" to dataSource.maxLifetime,
                "leakDetectionThreshold" to dataSource.leakDetectionThreshold,
                "validationTimeout" to dataSource.validationTimeout
            )
        } else {
            mapOf(
                "error" to "DataSource is not HikariDataSource",
                "type" to dataSource.javaClass.simpleName
            )
        }
    }
    
    @GetMapping("/hikari/config")
    fun getHikariConfig(): Map<String, Any> {
        return if (dataSource is HikariDataSource) {
            val hikariDataSource = dataSource
            
            mapOf(
                "jdbcUrl" to hikariDataSource.jdbcUrl,
                "username" to hikariDataSource.username,
                "driverClassName" to hikariDataSource.driverClassName,
                "poolName" to hikariDataSource.poolName,
                "maximumPoolSize" to hikariDataSource.maximumPoolSize,
                "minimumIdle" to hikariDataSource.minimumIdle,
                "connectionTimeout" to hikariDataSource.connectionTimeout,
                "idleTimeout" to hikariDataSource.idleTimeout,
                "maxLifetime" to hikariDataSource.maxLifetime,
                "leakDetectionThreshold" to hikariDataSource.leakDetectionThreshold,
                "validationTimeout" to hikariDataSource.validationTimeout,
                "connectionTestQuery" to hikariDataSource.connectionTestQuery,
                "connectionInitSql" to hikariDataSource.connectionInitSql,
                "isAutoCommit" to hikariDataSource.isAutoCommit,
                "isReadOnly" to hikariDataSource.isReadOnly,
                "catalog" to hikariDataSource.catalog,
                "schema" to hikariDataSource.schema,
                "transactionIsolation" to hikariDataSource.transactionIsolation,
                "isRunning" to hikariDataSource.isRunning,
                "isClosed" to hikariDataSource.isClosed
            )
        } else {
            mapOf(
                "error" to "DataSource is not HikariDataSource",
                "type" to dataSource.javaClass.simpleName
            )
        }
    }
    
    @GetMapping("/hikari/health")
    fun getHikariHealth(): Map<String, Any?> {
        return try {
            if (dataSource is HikariDataSource) {
                val hikariDataSource = dataSource
                val poolStats = hikariDataSource.hikariPoolMXBean
                
                val totalConnections = poolStats.totalConnections
                val activeConnections = poolStats.activeConnections
                val idleConnections = poolStats.idleConnections
                val awaitingConnections = poolStats.threadsAwaitingConnection
                val maxPoolSize = hikariDataSource.maximumPoolSize
                
                val utilizationPercentage = if (maxPoolSize > 0) {
                    (totalConnections.toDouble() / maxPoolSize * 100).toInt()
                } else 0
                
                val status = when {
                    awaitingConnections > 0 -> "WARNING"
                    utilizationPercentage > 80 -> "WARNING"
                    totalConnections > 0 -> "HEALTHY"
                    else -> "IDLE"
                }
                
                mapOf(
                    "status" to status,
                    "poolUtilization" to "${utilizationPercentage}%",
                    "connections" to mapOf(
                        "active" to activeConnections,
                        "idle" to idleConnections,
                        "total" to totalConnections,
                        "awaiting" to awaitingConnections,
                        "max" to maxPoolSize
                    ),
                    "alerts" to buildList {
                        if (awaitingConnections > 0) {
                            add("$awaitingConnections threads waiting for connections")
                        }
                        if (utilizationPercentage > 80) {
                            add("High pool utilization: ${utilizationPercentage}%")
                        }
                        if (totalConnections == maxPoolSize) {
                            add("Pool at maximum capacity")
                        }
                    }
                )
            } else {
                mapOf(
                    "status" to "UNKNOWN",
                    "error" to "DataSource is not HikariDataSource"
                )
            }
        } catch (e: Exception) {
            mapOf(
                "status" to "ERROR",
                "error" to e.message
            )
        }
    }
}