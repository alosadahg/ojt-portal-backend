package com.ojtportal.api.config.generator;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

public class OJTRecordGenerator implements IdentifierGenerator {

    @Override
    public String generate(SharedSessionContractImplementor session, Object object) {
        return "OJR-" + PKGenerator.generate("ojt_record");
    }
    
}
