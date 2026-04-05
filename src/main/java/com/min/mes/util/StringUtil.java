package com.min.mes.util;

public class StringUtil {

    public static String checkNull(Object obj){
        String outPut = obj == null ? "" : obj.toString();
        return outPut;
    }
}
