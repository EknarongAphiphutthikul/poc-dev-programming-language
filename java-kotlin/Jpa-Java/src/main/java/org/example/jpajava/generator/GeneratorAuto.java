package org.example.jpajava.generator;

import lombok.extern.slf4j.*;
import org.example.jpajava.utils.*;
import org.hibernate.engine.spi.*;
import org.hibernate.id.*;

@Slf4j
public class GeneratorAuto implements IdentifierGenerator {

    @Override
    public Object generate(SharedSessionContractImplementor sharedSessionContractImplementor, Object o) {
        String query = String.format("select NEXTVAL(%s_seq)", CommonUtils.camelToSnake(o.getClass().getSimpleName()));
        return sharedSessionContractImplementor.createNativeQuery(query, Integer.class).getSingleResult();
    }

}
