package com.javaref.spring;

public class A {
	private String name;

	public A() {
		super();
		// A并没有像前几个demo那样，通过某个属性直接依赖B，而是间接地使用了B的静态变量
		// 因此尽管没有依赖注入，B仍然需要在A之前先被初始化（即使是静态常量，也需要先初始化一次）
		System.out.println("A depends on:  B.hasInitializedObject = " + B.hasInitializedObject);
		System.out.println("A init.");
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
