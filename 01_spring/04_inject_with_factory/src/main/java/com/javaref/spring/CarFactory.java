package com.javaref.spring;

// 简单工厂
public class CarFactory {
    public CarFactory() {
        super();
        System.out.println("new CarFactory()");
    }

    public Car getCar(String name) throws Exception {
        return CarFactory.getCarStatic(name);
    }

    public static Car getCarStatic(String name) throws Exception {
        if (name.equals("audi")) {
            return new Audi();
        } else {
            throw new Exception("car name not supported");
        }
        // 高级一点可以用forClass()字符串转对象
    }
}
