package com.javaproref.kafka.apidemo.common;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;

public class UserDefinedDeSerializer implements Deserializer<Object> {
    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        System.out.println(this.getClass().getCanonicalName() + " configure");
    }

    @Override
    public Object deserialize(String topic, byte[] data) {
        return SerializationUtils.deserialize(data);
    }

    @Override
    public void close() {
        System.out.println(this.getClass().getCanonicalName() + " close");
    }
}
