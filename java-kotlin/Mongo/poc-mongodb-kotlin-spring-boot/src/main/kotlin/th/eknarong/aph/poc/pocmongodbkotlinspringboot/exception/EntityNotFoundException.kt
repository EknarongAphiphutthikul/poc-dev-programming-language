package th.eknarong.aph.poc.pocmongodbkotlinspringboot.exception

/**
 * Exception thrown when an entity is not found in the database
 *
 * @param entityName the name of the entity that was not found
 * @param identifier the identifier used to search for the entity
 */
class EntityNotFoundException(
    entityName: String,
    identifier: Any
) : RuntimeException("$entityName with identifier '$identifier' not found")