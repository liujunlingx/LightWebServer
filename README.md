# LightWebServer

LightWebServer是一个轻量级的嵌入式web服务器，同时提供了简单的mvc框架。

# Required
- Java version >= 1.8

# Install
1. -Dmaven.test.skip=true clean install
2. include maven dependency
```
<dependencies>
    <dependency>
        <groupId>com.lightwebserver</groupId>
        <artifactId>lightwebserver</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>
</dependencies>
```

# Usage

LightWebServer is similar to Spring MVC.

#### 请求映射

@Controller --> 类似Spring MVC中的@Controller

@WebPath    --> 类似Spring MVC中的@RequestMapping

#### 参数注入
- @QueryParam 得到GET参数
- @FormParam 得到x-www-form-urlencoded类型POST方法的参数
- @MultiPartData 得到multipart/form-data类型POST方法得到的参数
- HttpServletRequest无需注解即可注入
- 目前只能返回com.light.http.responses.Response

#### Example

1. Create controller
```
import com.light.http.HttpMethod;
import com.light.http.HttpStatus;
import com.light.http.requests.MimeData;
import com.light.http.responses.FileResponse;
import com.light.http.responses.JsonResponse;
import com.light.http.responses.Response;
import com.light.mvc.annotations.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Controller
public class HelloWorldController {

    // GET
    @WebPath("/testGet")
    public Response testGet(@QueryParam(value = "name",required = true)String name,Request request){
        Map<String,String> map = new HashMap<>();
        map.put("hello",name);
        return new JsonResponse(HttpStatus.OK_200,map);
    }

    // POST 请求的Content-Type为multipart/form-data
    @WebPath(value = "/testPost1",method = HttpMethod.POST)
    public Response testPost1(@MultiPartData("photo")MimeData photo,
                              @MultiPartData("name")MimeData name,
                              @MultiPartData("age")MimeData age){
        System.out.println("hello " + new String(name.getData()) + ", your age is " + new String(age.getData()));
        byte[] data = photo.getData();
        try {
            File file = File.createTempFile("the",".jpeg");
            FileOutputStream os = new FileOutputStream(file);
            os.write(data);
            os.close();
            return new FileResponse(HttpStatus.OK_200,file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new Response(HttpStatus.OK_200);
    }

    // POST 请求的Content-Type为application/x-www-form-urlencoded
    @WebPath(value = "/testPost2", method = HttpMethod.POST)
    public Response testPost2(@FormParam("name")String name, @QueryParam("age")Integer age){
        System.out.println("hello " + name + ", your age is " + age);
        return new Response(HttpStatus.OK_200);
    }

}
```

2. Start Server

```java
import com.light.io.Server;
import java.io.IOException;

public class Application {

    public static void main(String[] args) throws IOException {
        //第二个参数指定扫描@Controller的包名，可以是多个 
        Server.run(new String[]{"start","localhost:8080"},
                new String[]{"your_controller_pkg_path1","your_controller_pkg_path2"});
    }
}
```