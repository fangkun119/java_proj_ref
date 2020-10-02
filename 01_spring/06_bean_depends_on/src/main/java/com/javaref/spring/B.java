package com.javaref.spring;

public class B {
	public static boolean hasInitializedObject = false;

	public B() {
		super();
		System.out.println("B init.");
		hasInitializedObject = true;
	}
}
