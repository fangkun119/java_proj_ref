package com.javaref.spring;

public class B {
	private C singletonC;
	public C getSingletonC() {
		return singletonC;
	}
	public void setSingletonC(C c) {
		this.singletonC = c;
	}
	public B() {
		super();
		System.out.println("B Init.");
	}
}
