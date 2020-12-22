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

package com.javaprojref.spring_kafka.transaction_demo;

import com.javaprojref.spring_kafka.transaction_demo.config.KafkaConfig;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.support.converter.BatchMessagingMessageConverter;
import org.springframework.kafka.support.converter.RecordMessageConverter;
import org.springframework.kafka.support.converter.StringJsonMessageConverter;

import java.util.concurrent.CountDownLatch;

/**
 * Sample showing a batch listener and transactions.
 *
 * @author Gary Russell
 * @since 2.2.1
 *
 */
@SpringBootApplication(scanBasePackages = "com.javaprojref.spring_kafka.transaction_demo")
public class Application {
	public final static CountDownLatch APP_CLOSE_LATCH = new CountDownLatch(1);

	public static void main(String[] args) throws InterruptedException {
		ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);
		Application.APP_CLOSE_LATCH.await();
		Thread.sleep(5_000);
		context.close();
	}
}
