package com.light.util;

import com.light.http.requests.MimeData;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Created on 2018/4/26.
 */
public class ReflectUtil {

    private static final Map<Class<?>,Function<Object,Object>> TYPE_FUNCTION_MAP;
    private static final Function<Object,Object> DEFAULT_FUNC = s -> null;

    static {
        TYPE_FUNCTION_MAP = new HashMap<>();
        //TYPE_FUNCTION_MAP.put(Boolean.class,);
        Function<Object,Object> boolFunc = s -> Boolean.valueOf((String) s);
        Function<Object,Object> charFunc = s -> ((String)s).charAt(0);
        Function<Object,Object> byteFunc = s -> Byte.valueOf((String) s);
        Function<Object,Object> shortFunc = s -> Short.valueOf((String) s);
        Function<Object,Object> integerFunc = s -> Integer.valueOf((String) s);//TODO 使用valueOf和parseInt的性能比较
        Function<Object,Object> longFunc = s -> Long.valueOf((String) s);
        Function<Object,Object> floatFunc = s -> Float.valueOf((String) s);
        Function<Object,Object> doubleFunc = s -> Double.valueOf((String) s);

        TYPE_FUNCTION_MAP.put(Boolean.class,boolFunc);
        TYPE_FUNCTION_MAP.put(Character.class,charFunc);
        TYPE_FUNCTION_MAP.put(Byte.class,byteFunc);
        TYPE_FUNCTION_MAP.put(Short.class,shortFunc);
        TYPE_FUNCTION_MAP.put(Integer.class,integerFunc);
        TYPE_FUNCTION_MAP.put(Long.class,longFunc);
        TYPE_FUNCTION_MAP.put(Float.class,floatFunc);
        TYPE_FUNCTION_MAP.put(Double.class,doubleFunc);

        TYPE_FUNCTION_MAP.put(String.class,String::valueOf);
        TYPE_FUNCTION_MAP.put(MimeData.class, o -> o);

        //TODO Date类型
    }

    public static Object parseObj(Object val,Class<?> type){
        if(val == null)
            return null;
        return TYPE_FUNCTION_MAP.getOrDefault(type,DEFAULT_FUNC).apply(val);
    }
}
