package com.javaref.spring;

public class C {
	private A singletonA;
	public C() {
		super();
		System.out.println("C Init.");
	}
	public A getSingletonA() {
		return singletonA;
	}
	public void setSingletonA(A a) {
		this.singletonA = a;
	}
}
