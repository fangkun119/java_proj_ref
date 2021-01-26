package com.javaref.spring;

public class C {
	private A a;

	public C() {
		super();
		System.out.println("C Init.");
	}

	public A getA() {
		return a;
	}

	public void setA(A a) {
		this.a = a;
	}
}
