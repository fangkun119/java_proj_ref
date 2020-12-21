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

package com.javaprojref.spring_kafka.multi_method_demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.javaprojref.spring_kafka.multi_method_demo.domain.producer.Bar1;
import com.javaprojref.spring_kafka.multi_method_demo.domain.producer.Foo1;

/**
 * @author Gary Russell
 * @since 2.2.1
 *
 */
@RestController
public class Controller {
	@Autowired
	private KafkaTemplate<Object, Object> template;

	@PostMapping(path = "/send/foo/{message}")
	public void sendFoo(@PathVariable String message) {
		this.template.send("foos", new Foo1(message));
	}

	@PostMapping(path = "/send/bar/{message}")
	public void sendBar(@PathVariable String message) {
		this.template.send("bars", new Bar1(message));
	}

	@PostMapping(path = "/send/unknown/{message}")
	public void sendUnknown(@PathVariable String message) {
		this.template.send("bars", message);
	}
}
