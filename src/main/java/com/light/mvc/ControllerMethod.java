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

    public Response handle(Request request) throws InvocationTargetException, IllegalAccessException {
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
                objects[i] = processPojo();
            }else{
                Annotation annotation = annotations[0];
                Object o = null;
                if(annotation instanceof QueryParam){
                    o = processQueryParam(annotation,request,type);
                }else if(annotation instanceof FormParam){
                    o = processFormParam(annotation,request,type);
                }else if(annotation instanceof MultiPartData){
                    o = processMultiPartData(annotation,request,type);
                }

                if(o != null && o instanceof HttpServletResponse){
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

    //TODO 普通参数通过javaBean绑定机制
    private Object processPojo(){
        return null;
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
}
