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

/**
 * Created on 2018/4/23.
 */
@Controller
@WebPath("/")
public class HelloWorldController {

    @WebPath(value = "/testPost1",method = HttpMethod.POST)
    public Response testPost1(@MultiPartData("photo")MimeData photo,
                              @MultiPartData("name")MimeData name,
                              @MultiPartData("age")MimeData age){
        System.out.println("hello " + new String(name.getData()) + ", your age is " + new String(age.getData()));
        byte[] data = photo.getData();
        try {
            //File file = File.createTempFile("abc",".jpeg");
            File file = new File("F://abc.jpg");
            FileOutputStream os = new FileOutputStream(file);
            os.write(data);
            os.close();
            return new FileResponse(HttpStatus.OK_200,file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new Response(HttpStatus.OK_200);
    }

    @WebPath(value = "/testPost2", method = HttpMethod.POST)
    public Response testPost2(@FormParam("name")String name, @QueryParam("age")Integer age){
        System.out.println("hello " + name + ", your age is " + age);
        return new Response(HttpStatus.OK_200);
    }

    @WebPath("/testGet")
    public Response testGet(@QueryParam("name")String name, @QueryParam("age")Integer age){
        System.out.println("hello " + name + ", your age is " + age);
        File file = new File("F://abc.jpg");
        return new FileResponse(HttpStatus.OK_200,file);
        //return new Response(HttpStatus.OK_200);
    }

    //TODO name传不了中文
    @WebPath("/testJson")
    public Response testJson(@QueryParam(value = "name",required = true)String name){
        Map<String,String> map = new HashMap<>();
        map.put("hello",name);
        return new JsonResponse(HttpStatus.OK_200,map);
    }
}