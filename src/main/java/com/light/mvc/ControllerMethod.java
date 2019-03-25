package com.light.mvc;

import com.light.http.HttpMethod;
import com.light.http.HttpStatus;
import com.light.http.requests.MimeData;
import com.light.http.requests.Request;
import com.light.http.responses.Response;
import com.light.mvc.annotations.FormParam;
import com.light.mvc.annotations.MultiPartData;
import com.light.mvc.annotations.QueryParam;
import com.light.util.ReflectUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * Created on 2018/4/23.
 */
@Data
@AllArgsConstructor
public class ControllerMethod {

    private Object controller;
    private Method method;
    private HttpMethod[] methods;

    public Response handle(Request request) throws InvocationTargetException, IllegalAccessException, InstantiationException {
        Parameter[] parameters = method.getParameters();
        Object[] objects = new Object[parameters.length];
        for(int i = 0;i < parameters.length; i++){
            Parameter parameter = parameters[i];
            Class<?> type = parameter.getType();
            if(type == HttpServletRequest.class || type == Request.class){
                objects[i] = request;
                continue;
            }

            Annotation[] annotations = parameter.getAnnotations();

            if(annotations.length == 0){
                //被@WebPath注解的方法的参数，如果没有注解，则当普通java bean处理
                objects[i] = processPojo(type,request);//may throw InstantiationException
            }else{
                Annotation annotation = annotations[0];
                Object o = null;
                if(annotation instanceof QueryParam){
                    o = processQueryParam(annotation,request,type);
                }
                if(annotation instanceof FormParam){
                    o = processFormParam(annotation,request,type);
                }
                if(annotation instanceof MultiPartData){
                    o = processMultiPartData(annotation,request,type);
                }
                if(o instanceof HttpServletResponse){
                    return (Response) o; //may return bad_request_400
                }
                objects[i] = o;
            }
        }
        return (Response) method.invoke(controller,objects);
    }

    public boolean containHttpMethod(String method) {
        for(HttpMethod httpMethod : methods){
            if(method.equals(httpMethod.getName()))
                return true;
        }
        return false;
    }

    /**
     * 普通参数通过javaBean绑定机制
     * @param type 参数类类型
     * @param request 本次请求的request
     * @return
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws InvocationTargetException
     */
    private Object processPojo(Class<?> type,Request request) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        Object object = type.newInstance();//new 出这个对象
        Method[] methods = type.getMethods();
        for(Method method : methods){
            String methodName = method.getName();
            if(!methodName.startsWith("set") || method.getName().length() == 3 || method.getParameters().length != 1){
                continue;
            }
            String parameterName = method.getName().substring(3);
            //生成的set方法是驼峰命名，比如setName、setAge、setOrderNumber,而变量命名一般是name、age、orderNumber,所以需要首字母小写
            parameterName = firstLetterLowerCase(parameterName);
            Object parameterValueStr = request.getParameter(parameterName);
            Parameter parameter = method.getParameters()[0];
            Object parameterValue = ReflectUtil.parseObj(parameterValueStr,parameter.getType());
            if(parameterValue == null){
                continue;
            }
            method.invoke(object,parameterValue);//调用set方法，构造变量的值
        }
        return object;
    }

    private Object processQueryParam(Annotation annotation,Request request,Class<?> type){
        QueryParam queryParam = (QueryParam) annotation;
        String value = request.getParameter(queryParam.value());
        if(queryParam.required() && StringUtils.isEmpty(value)){
            return new Response(HttpStatus.BAD_REQUEST_400);
        }
        return ReflectUtil.parseObj(value,type);
    }

    private Object processFormParam(Annotation annotation,Request request,Class<?> type){
        FormParam formParam = (FormParam) annotation;
        String value = request.getParameter(formParam.value());
        if(formParam.required() && StringUtils.isEmpty(value)){
            return new Response(HttpStatus.BAD_REQUEST_400);
        }
        return ReflectUtil.parseObj(value,type);
    }

    private Object processMultiPartData(Annotation annotation,Request request,Class<?> type){
        MultiPartData multiPartData = (MultiPartData) annotation;
        MimeData mimeData = request.getRequestBody().getMimeMap().get(multiPartData.value());
        if(multiPartData.required() && mimeData == null){
            return new Response(HttpStatus.BAD_REQUEST_400);
        }
        return mimeData;
    }

    private String firstLetterLowerCase(String input){
        char[] c = input.toCharArray();
        c[0] = Character.toLowerCase(c[0]);
        return new String(c);
    }
}
