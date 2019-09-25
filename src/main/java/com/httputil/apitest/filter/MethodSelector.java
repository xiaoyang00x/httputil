package com.httputil.apitest.filter;

import com.httputil.apitest.testcase.TestCaseListener;
import com.httputil.apitest.util.PropertyUtil;
import com.httputil.apitest.util.StringUtil;
import org.testng.IMethodInstance;
import org.testng.IMethodInterceptor;
import org.testng.ITestContext;
import org.testng.ITestNGMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class MethodSelector implements IMethodInterceptor {
    private ArrayList<String> cidList = new ArrayList<String>();
    private ArrayList<String> httpurlList = new ArrayList<String>();
    private ArrayList<String> levelList = new ArrayList<String>();
    private ArrayList<String> authorList = new ArrayList<String>();


    public MethodSelector() {

        //初始化运行列表
        String cid = PropertyUtil.getProperty("cid");
        String httpurl = PropertyUtil.getProperty("httpurl");
        String level = PropertyUtil.getProperty("level");
        String author = PropertyUtil.getProperty("author");
        if (!StringUtil.isEmpty(cid)) {
            for (String tem : cid.split(",")) {
                cidList.add(tem);
            }
        }

        if (!StringUtil.isEmpty(httpurl)) {
            for (String tem : httpurl.split(",")) {
                httpurlList.add(tem);
            }
        }

        if (!StringUtil.isEmpty(level)) {
            for (String tem : level.split(",")) {
                levelList.add(tem);
            }
        }

        if (!StringUtil.isEmpty(author)) {
            for (String tem : author.split(",")) {
                authorList.add(tem);
            }
        }

    }

    @Override
    public List<IMethodInstance> intercept(List<IMethodInstance> methods, ITestContext context) {
        List<IMethodInstance> updatedMethodInstance = new ArrayList<IMethodInstance>();
        for (IMethodInstance method : methods) {
            ITestNGMethod testMethod = method.getMethod();
            try {
                if (isExpectedMethod(testMethod))
                    updatedMethodInstance.add(method);
            } catch (Exception e) {
            }

        }
        return updatedMethodInstance;

    }


    /**
     * 属于预期的用例加入到执行计划中
     *
     * @param method
     * @return
     */
    private boolean isExpectedMethod(ITestNGMethod method) {

        TestCaseListener testCaseListener = method.getConstructorOrMethod().getMethod().getAnnotation(TestCaseListener.class);
        String cid = testCaseListener.cid();
        String httpurl = testCaseListener.httpurl();
        String level = testCaseListener.level();
        String author = testCaseListener.author();

        if (!cidList.isEmpty() && cidList.contains(cid))
            return true;


        if (!httpurlList.isEmpty() && httpurlList.contains(httpurl))
            return true;


        if (!levelList.isEmpty() && levelList.contains(level))
            return true;


        if (!authorList.isEmpty() && authorList.contains(author))
            return true;

        if (!StringUtil.isEmpty(PropertyUtil.getProperty("methodSelector")) && PropertyUtil.getProperty("methodSelector").equals("true"))
            return false;
        else
            return true;
    }
}
