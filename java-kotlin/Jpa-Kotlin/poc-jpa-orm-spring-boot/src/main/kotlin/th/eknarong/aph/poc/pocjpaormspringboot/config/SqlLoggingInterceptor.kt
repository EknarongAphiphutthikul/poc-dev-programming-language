package th.eknarong.aph.poc.pocjpaormspringboot.config

import org.hibernate.Interceptor
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.io.Serializable

@Component
class SqlLoggingInterceptor : Interceptor, Serializable {
    
    private val logger = LoggerFactory.getLogger(SqlLoggingInterceptor::class.java)
    
    override fun onLoad(entity: Any?, id: Any?, state: Array<Any?>?, propertyNames: Array<String>?, types: Array<org.hibernate.type.Type>?): Boolean {
        logger.debug("Loading entity: {} with id: {}", entity?.javaClass?.simpleName, id)
        return super.onLoad(entity, id, state, propertyNames, types)
    }
    
    override fun onPersist(entity: Any?, id: Any?, state: Array<Any?>?, propertyNames: Array<String>?, types: Array<org.hibernate.type.Type>?): Boolean {
        logger.debug("Saving entity: {} with id: {}", entity?.javaClass?.simpleName, id)
        logEntityState(entity, state, propertyNames, "SAVE")
        return super.onPersist(entity, id, state, propertyNames, types)
    }
    
    override fun onFlushDirty(entity: Any?, id: Any?, currentState: Array<Any?>?, previousState: Array<Any?>?, propertyNames: Array<String>?, types: Array<org.hibernate.type.Type>?): Boolean {
        logger.debug("Updating entity: {} with id: {}", entity?.javaClass?.simpleName, id)
        logEntityState(entity, currentState, propertyNames, "UPDATE")
        return super.onFlushDirty(entity, id, currentState, previousState, propertyNames, types)
    }
    
    override fun onRemove(entity: Any?, id: Any?, state: Array<Any?>?, propertyNames: Array<String>?, types: Array<org.hibernate.type.Type>?) {
        logger.debug("Deleting entity: {} with id: {}", entity?.javaClass?.simpleName, id)
        super.onRemove(entity, id, state, propertyNames, types)
    }
    
    private fun logEntityState(entity: Any?, state: Array<Any?>?, propertyNames: Array<String>?, operation: String) {
        if (logger.isDebugEnabled && entity != null && state != null && propertyNames != null) {
            val entityName = entity.javaClass.simpleName
            logger.debug("=== {} Operation for {} ===", operation, entityName)
            
            propertyNames.forEachIndexed { index, propertyName ->
                val value = if (index < state.size) state[index] else null
                val valueStr = when(value) {
                    null -> "NULL"
                    is String -> "'$value'"
                    is Number -> value.toString()
                    is Boolean -> value.toString()
                    else -> value.toString()
                }
                logger.debug("  {}: {}", propertyName, valueStr)
            }
            logger.debug("=== End {} Operation ===", operation)
        }
    }
}