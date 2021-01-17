package com.javaref.springboot.entity;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**********************
 * 系统配置相关，配置为一个bean，这样可以注入到其他的类中
 */
@Component
public class Config {
	// systemName从配置文件中注入，
	// 使用不同的配置文件，将得到不同的配置值，从而做到不同环境的配置文件分开
	@Value(value = "${config.systemName}")
	private String systemName;

	public String getSystemName() {
		return systemName;
	}

	public void setSystemName(String systemName) {
		this.systemName = systemName;
	}
}
