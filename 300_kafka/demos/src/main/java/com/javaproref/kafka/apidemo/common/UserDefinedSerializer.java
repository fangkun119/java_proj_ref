package com.javaproref.kafka.apidemo.common;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.kafka.common.serialization.Serializer;

import java.io.Serializable;
import java.util.Map;

public class UserDefinedSerializer implements Serializer<Object> {
    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        System.out.println(this.getClass().getCanonicalName() + " configure");
    }

    @Override
    public byte[] serialize(String topic, Object data) {
        return SerializationUtils.serialize((Serializable) data);
    }

    @Override
    public void close() {
        System.out.println(this.getClass().getCanonicalName() + " close");
    }
}
