package test.javaprojref.springinternal.demo2autowire;

import com.javaprojref.springinternal.demo2autowire.UserController;
import com.javaprojref.springinternal.demo2autowire.Autowired; // 是自己编写的Autowired注解
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.stream.Stream;

public class Demo2AutowiredTest {
    /**
     * 演示通过注解来注入成员变量：
     * (1) @Autowired是自己编写的注解、用来标记某个变量（可以是私有变量）希望被注入
     * (2) demo2autowire.UserController的userService字段使用@Autowired注解
     * (3) 被注入的对象通过反射调用默认构造函数生成（没有bean容器）
     */
    @Test
    public void testSettingMethod() throws NoSuchFieldException {
        UserController userController = new UserController();
        Class<? extends UserController> cls = userController.getClass();

        Stream.of(cls.getDeclaredFields()).forEach(field -> {
            Autowired annotation = field.getAnnotation(Autowired.class);
            if (null != annotation) {
                System.out.println("processing " + field.getName());

                Class<?> type = field.getType();
                try {
                    // 通过反射调用默认构造函数，创建被注入的对象
                    Object injectedObject = type.getConstructor().newInstance();
                    // 让私有变量可以通过反射来访问
                    field.setAccessible(true);
                    // 通过反射注入
                    field.set(userController, injectedObject);
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
            }
        });

        System.out.println(userController.getUserService());
        // 输出：.UserService@6956de9
    }
}
