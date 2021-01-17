package com.javaref.spring;

public class A {
	private B b;

	public A() {
		super();
		System.out.println("A Init.");
	}
	public B getB() {
		return b;
	}

	public void setB(B b) {
		this.b = b;
	}
}
