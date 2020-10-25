package com.javaref.springboot04.controller;

import com.javaref.springboot04.domain.City;
import com.javaref.springboot04.service.CityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

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

	@Autowired
	CityService citySrv;

	//${method_uri}是"/list"
	@RequestMapping("/list")
	//ModelMap可以用于传递数据给模板引擎、在生成页面是使用；
	// 也可以使用Model
	// 区别是ModelMap增加了map的get、put等操作
	public String list(ModelMap map) {
		List<City> cities = citySrv.findAll();
		map.addAttribute("cities", cities);
 		return "list";
	}

	@PostMapping("/add")
	public String add(@RequestParam("id") Integer id, @RequestParam("name") String name, Model map) {
		String result = citySrv.add(City.create(id, name));
		map.addAttribute("result", result);
		return "add";
	}

	// 如果需要用 @RequestParam 注解的参数太多，也可以用 @ModelAttribute 注解一个封装了参数的对象
	@PostMapping("/add2")
	public String add2(@ModelAttribute City city, Model map) {
		String result = "";
		if (null == city.getId() || null == city.getName()) {
			result = "参数错误";
		} else {
			result = citySrv.add(city);
		}
		map.addAttribute("result", result);
		return "add";
	}

	@RequestMapping("/addPage")
	public String addPage() {
		return "add";
	}
}
