/*
 * Copyright 2018-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.javaprojref.spring_kafka.multi_method_demo.config;

import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.javaprojref.spring_kafka.multi_method_demo.domain.consumer.Bar2;
import com.javaprojref.spring_kafka.multi_method_demo.domain.consumer.Foo2;

// 使用KafkaListener接收数据，参考
// https://docs.spring.io/spring-kafka/docs/2.5.10.RELEASE/reference/html/#kafka-listener-annotation
@Component
@KafkaListener(id = "multiGroup", topics = { "foos", "bars" })
public class KafkaListenerConfig {
	@KafkaHandler
	public void foo(Foo2 foo) {
		// 业务逻辑变复杂时可进步一拆分到service层，参考项目：sample-03
		System.out.println("Received: " + foo);
	}

	@KafkaHandler
	public void bar(Bar2 bar) {
		System.out.println("Received: " + bar);
	}

	@KafkaHandler(isDefault = true)
	public void unknown(Object object) {
		System.out.println("Received unknown: " + object);
	}
}
