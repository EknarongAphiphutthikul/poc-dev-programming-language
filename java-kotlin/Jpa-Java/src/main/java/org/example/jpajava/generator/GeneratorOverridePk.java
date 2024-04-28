package org.example.jpajava.generator;

import lombok.extern.slf4j.*;
import org.hibernate.engine.spi.*;
import org.hibernate.id.*;

@Slf4j
public class GeneratorOverridePk implements IdentifierGenerator {

    @Override
    public Object generate(SharedSessionContractImplementor sharedSessionContractImplementor, Object o) {
        Object id = sharedSessionContractImplementor.getEntityPersister(o.getClass().getName(), o).getIdentifier(o, sharedSessionContractImplementor);
        if (id == null || (Integer) id == 0) {
            // return custom id by something like
        }
        return id;
    }

}
