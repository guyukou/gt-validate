package com.prodaas.util;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

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
            return (String) invoke.invokeFunction("f", key1);
        } catch (ScriptException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) throws ScriptException, NoSuchMethodException {

    }
}
