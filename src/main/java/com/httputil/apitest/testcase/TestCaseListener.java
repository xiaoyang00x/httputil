package com.httputil.apitest.testcase;


import com.google.common.collect.Lists;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface TestCaseListener {


    //caseId
    public String cid() default "";


    //http请求url path
    public String path() default "";


    //用例级别 p0、p1、p2
    public String level() default "";


    //case作者
    public String author() default "";


    //case描述
    public String description() default "";

    //接口类型 rpc/http
    public String type() default "";


}
