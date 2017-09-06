package com.prodaas.util;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by guyu on 2017/3/9.
 */
public class JSEngine {
    private static Invocable invoke;

    static {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("javascript");
        String jsFileName = "js/func.js";   // 读取js文件
        InputStream in = JSEngine.class.getClassLoader().getResourceAsStream(jsFileName);

        try {
            engine.eval(new InputStreamReader(in));
        } catch (ScriptException e) {
            e.printStackTrace();
        }

        if (engine instanceof Invocable) {
            invoke = (Invocable) engine;    // 调用merge方法，并传入两个参数
        }

        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void init() {

    }

    public static String userResponse(String key1, String key2) {

        try {
            return (String) invoke.invokeFunction("user_response", key1, key2);
        } catch (ScriptException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getA(String key1) {

        try {
            return (String) invoke.invokeFunction("getA", key1);
        } catch (ScriptException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getA(String trail,String arr,String seq) {
        try {
            return (String) invoke.invokeFunction("furtherAuthen", trail,arr,seq);
        } catch (ScriptException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) throws ScriptException, NoSuchMethodException {
        String s = "20,24,0,1,0,86,2,0,17,4,0,17,2,0,16,2,1,17,1,0,16,3,1,17,4,2,16,2,0,18,6,0,16,6,0,16,4,0,17,6,0,16,4,1,18,10,1,16,14,0,17,16,0,18,25,2,15,9,0,18,8,0,16,7,0,16,5,0,18,6,0,17,4,0,17,4,0,17,1,0,16,1,0,16,2,0,27,2,0,24,2,0,30,1,0,40,1,0,14,-1,0,518,-1,0,125,0,0,214";
        Pattern p = Pattern.compile("-?\\d+,-?\\d+,-?\\d+");
        Matcher matcher = p.matcher(s);
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        while (matcher.find()) {
            sb.append("[");
            sb.append(matcher.group());
            sb.append("],");
        }
        sb.replace(sb.length() - 1, sb.length(), "]");

        System.out.println(sb.toString());
        System.out.println(getA(sb.toString(),"[12, 58, 98, 36, 43, 95, 62, 15, 12]","4b437078"));
    }
}
