### 编译

> 用-U参数编译
> 
> ~~~bash
> mvn -U clean install
> ~~~
> 
> 或者在IDEA的maven配置中勾选"always update snapshot"
> 
> 参考：[https://blog.csdn.net/cobracanary/article/details/103506967](https://blog.csdn.net/cobracanary/article/details/103506967)

### 测试

> 启动程序后运行如下命令
> 
> ~~~bash
> $ curl -X GET http://127.0.0.1:8080/test; echo "";
> 2021-05-25 15:57:24
> ~~~
> 



