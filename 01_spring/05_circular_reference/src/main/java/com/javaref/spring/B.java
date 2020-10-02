package com.javaref.spring;

public class B {
	private C c;

	public C getC() {
		return c;
	}

	public void setC(C c) {
		this.c = c;
	}

	public B() {
		super();
		System.out.println("B Init.");
	}
}
