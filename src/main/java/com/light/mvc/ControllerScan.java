package com.light.mvc;

import com.light.http.HttpMethod;
import com.light.http.requests.Request;
import com.light.mvc.annotations.Controller;
import com.light.mvc.annotations.WebPath;
import com.light.util.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.reflections.Reflections;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Created on 2018/4/23.
 */
@Slf4j
public class ControllerScan {

    private static Map<String,ControllerMethod> controllerMethodMap = Collections.synchronizedMap(new TreeMap<String, ControllerMethod>());

    public static void scanPackage(String pkgName){
        Reflections reflections = new Reflections(pkgName);

        Set<Class<?>> controllers = reflections.getTypesAnnotatedWith(Controller.class);

        for (Class<?> controller : controllers) {
            WebPath webPathOnClass = controller.getAnnotation(WebPath.class);
            String[] parentPath = new String[]{"/"};
            HttpMethod[] parentHttpMethod = null;
            if(webPathOnClass != null){
                if(webPathOnClass.value().length == 0){
                    log.warn("{}上的@WebPath value为空，已默认为/",controller.getName());
                }else{
                    parentPath = webPathOnClass.value();
                }
                parentHttpMethod = webPathOnClass.method();
            }

            Method[] methods = controller.getMethods();
            for(Method method : methods){
                WebPath webPathOnMethod = method.getAnnotation(WebPath.class);
                if(webPathOnMethod == null){
                    continue;
                }
                String[] childPath = webPathOnMethod.value();
                HttpMethod[] childHttpMethod = webPathOnMethod.method();
                if(ArrayUtils.isEmpty(childHttpMethod)){
                    if(ArrayUtils.isEmpty(parentHttpMethod)){
                        childHttpMethod = new HttpMethod[]{HttpMethod.GET};
                    }else{
                        childHttpMethod = parentHttpMethod;
                    }
                }

                ControllerMethod controllerMethod = null;
                try {
                    controllerMethod = new ControllerMethod(controller.newInstance(),method,childHttpMethod);
                } catch (InstantiationException e) {//controller.newInstance() 出错
                    e.printStackTrace();
                } catch (IllegalAccessException e) {//controller.newInstance() 出错
                    e.printStackTrace();
                }

                for(String parent : parentPath){
                    for(String child : childPath){
                        String path = FileUtil.combinePath(parent,child);
                        if(controllerMethodMap.containsKey(path)){
                            log.warn("RequestMapping 出现重复,映射路径为'{}', 将被覆盖!",path);
                        }
                        controllerMethodMap.put(path,controllerMethod);
                        log.info("成功注册请求方法映射,映射[{}]到[{}.{}]", path, controller.getName(), method.getName());
                    }
                }
            }
        }
    }

    public static ControllerMethod findController(Request request){
        String requestURI = request.getRequestURI();

        //requestURI与WebPath严格匹配
//        if(controllerMethodMap.containsKey(requestURI)){
//            return controllerMethodMap.get(requestURI);
//        }else{
//            return null;
//        }

        //requestURI与WebPath正则匹配
        for(Map.Entry<String,ControllerMethod> entry : controllerMethodMap.entrySet()){
            if(requestURI.matches(entry.getKey())){
                return entry.getValue();
            }
        }
        return null;
    }

}
