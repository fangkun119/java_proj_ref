package test.javaprojref.springinternal.demo1reflection;

import com.javaprojref.springinternal.demo1reflection.UserController;
import com.javaprojref.springinternal.demo1reflection.UserService;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

public class DemoTest {
    /**
     * class.getFields()        ：返回所有public属性（包含从父类继承得到的属性），不保证顺序
     * class.getDeclareFields() ：返回所有属性（包含protected、private属性，但不包含从父类继承得到的)，不保证顺序
     */
    @Test
    public void testGetDeclaredFields() {
        UserController userController = new UserController();
        Class<? extends UserController> cls = userController.getClass();
        // cls.getFields()：返回所有public属性（包含从父类继承得到的属性），不保证顺序
        // cls.getDeclareFields();	返回所有属性（包含pprotected、private属性，但不包含从父类继承得到的)，不保证顺序
        Field[] declaredFields = cls.getDeclaredFields();
        System.out.println(declaredFields.length);
        // 输出
        // 1
        Arrays.asList(declaredFields).stream().forEach(System.out::println);
        // 输出，说明getDeclaredFields可以返回private属性
        // private com.javaprojref.springinternal.demo1.UserService com.javaprojref.springinternal.demo1.UserController.userService
    }

    /*
     * 默认情况下不能使用反射直接set私有变量
     */
    @Test
    public void testSettingPrivateField() throws NoSuchFieldException {
        UserController userController = new UserController();
        Class<? extends UserController> cls = userController.getClass();
        Field userServiceField = cls.getDeclaredField("userService");
        UserService userService = new UserService();
        try {
            userServiceField.set(userController, userService);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            // 会走到这一行并输出，说明无法直接set private属性
            // java.lang.IllegalAccessException:
            //    class com.javaprojref.springinternal.demo1.test.DemoTest cannot access a member of class com.javaprojref.springinternal.demo1.UserController with modifiers "private"
            // at java.base/jdk.internal.reflect.Reflection.newIllegalAccessException(Reflection.java:385)
            //
            // 用`javap -verbose`查看`javac`编译好的二进制程序，可以看到私有变量在编译期已经标记成private
            // 但反射是在运行期执行、因此在运行期抛出异常
        }
        System.out.println(userController.getUserService());
    }

    /**
     * 借助field.setAccessible(true), 可以直接set私有变量
     * 这是@autowired的实现原理，不需要为私有变量编写get,set方法，也可以实现依赖注入
     */
    @Test
    public void testSettingPrivateField2() throws NoSuchFieldException {
        UserController userController = new UserController();
        Class<? extends UserController> cls = userController.getClass();
        Field userServiceField = cls.getDeclaredField("userService");
        // 原本是private变量，将其设置为"可访问"
        userServiceField.setAccessible(true);
        try {
            // 直接通过反射设置私有变量
            userServiceField.set(userController, new UserService());
            System.out.println(userController.getUserService());
            // 输出
            // com.javaprojref.springinternal.demo1.UserService@270421f5
        } catch (IllegalAccessException e) {
            // 没有抛出异常
            e.printStackTrace();
        }
    }

    /**
     * 约定field name和get,set方法的命名方式，通过field name找到set方法并调用
     * 这是属性注入的实现原理
     */
    @Test
    public void testSettingMethod() throws NoSuchFieldException {
        UserController userController = new UserController();
        Class<? extends UserController> cls = userController.getClass();
        // 字段
        Field userServiceField = cls.getDeclaredField("userService");
        // Field Name -> Set Method Name
        String fieldName = userServiceField.getName();
        String setMethodName = "set"
                + fieldName.substring(0, 1).toUpperCase()
                + fieldName.substring(1, fieldName.length());
        System.out.println(setMethodName); // 输出：setUserService
        // 使用Set Method设置
        try {
            Method setMethod = cls.getMethod(setMethodName, UserService.class);
            setMethod.invoke(userController, new UserService());
            System.out.println(userController.getUserService());
            // 输出
            // com.javaprojref.springinternal.demo1.UserService@5a42bbf4
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
