package com.javaref.spring;

public class A {
	private B singletonB;

	public A() {
		super();
		System.out.println("A Init.");
	}
	public B getSingletonB() {
		return singletonB;
	}

	public void setSingletonB(B b) {
		this.singletonB = b;
	}
}
