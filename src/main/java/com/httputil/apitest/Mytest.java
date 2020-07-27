package com.httputil.apitest;


import org.testng.annotations.Test;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

@Test
public class Mytest {
    public String name;

    public Mytest(String name) {

        System.out.println( "name" );

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public static void main(String[] args) throws Exception {

        //通过反射获取对象
        Mytest.class.getConstructor( String.class ).newInstance( "yangyu" );


        Method[] methods = Mytest.class.getMethods();

        for (Annotation m : Mytest.class.getAnnotations()){
            m.equals( "httpuri" );
            //获取 httpuri的值
            // httpclint RestAssured
            // client httpuri
            // client请求
        }


    }
}
