package com.javaref.springboot02.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @Controller : 加入Spring容器管理 (点击看该注解的代码，它其实就是一个@Component)、它是一个单例
 * @RestController : 相比@Controller，多了@ResponseBody，表示返回的时候不会渲染，而是直接将response返回给前端
 *
 * 访问 http://${hostname}:${server.port}/${server.servlet.context-path}/${controller_uri}/${method_uri} 时，
 * 会调用到对应的方法，本例中
 * 	server.port					：在application.properties中覆盖了默认值
 * 	server.servlet.context-path	：在application.properties中覆盖了默认值
 * 	controller_uri				：从MainController的@RequestMapping注解传入
 * 	method_uri					：从方法list的@RequestMapping注解传入
 *
 * list(ModelMap map)对应的方法，可通过 http://localhost:80/boot/user/list 来访问
 */

@Controller
@RequestMapping("/user")
public class MainController {
	/**
	 * @Controller 的方法返回String时，Spring会以这个String为名字，去查找模板文件，来生成返回页面
	 * 例如：list(ModelMap map)方法返回"list"时
	 * 		模板文件对应代码库的"src/resources/templates/${RequestMapping}.html"及list.html
	 * 		Spring从ModelMap中取出数据，填充到模板中，生成返回的页面
	 * 备注：
	 * 1. 这需要项目引入模板引擎，否则会返回一个提示错误的页面
	 * 2. 如果方法被@ResponseBody注解，会直接把return值用json序列化之后，返回给浏览器（不做模板渲染）
	 */

	@RequestMapping("/list")  //${method_uri}是"/list"
	public String list(ModelMap map) {
		// ModelMap是用来在Spring MVC context中传递数据的对象
		//
 		map.put("name", "Tom");
 		return "list";
	}
}
