package org.example.jpajava.generator;

import lombok.extern.slf4j.*;
import org.hibernate.engine.spi.*;
import org.hibernate.id.*;
import org.hibernate.service.*;
import org.hibernate.type.*;

import java.util.*;

@Slf4j
public class GeneratorSeq implements IdentifierGenerator, Configurable {

    private String seqName;

    @Override
    public Object generate(SharedSessionContractImplementor sharedSessionContractImplementor, Object o) {
        String query = String.format("select NEXTVAL(%s)", seqName);
        return sharedSessionContractImplementor.createNativeQuery(query, Integer.class).getSingleResult();
    }

    @Override
    public void configure(Type type, Properties properties, ServiceRegistry serviceRegistry) {
        seqName = properties.getProperty("seq-name");
    }

}
