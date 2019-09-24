package com.httputil.apitest.filter;

import com.httputil.apitest.util.PropertyUtil;
import org.testng.IMethodInstance;
import org.testng.IMethodInterceptor;
import org.testng.ITestContext;
import org.testng.ITestNGMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

public class MethodSelector implements IMethodInterceptor {
    private ArrayList<String> testcaseIDList = new ArrayList<String>();

    /**
     * Constractor
     * <p>
     * init testMehtod list
     */
    public MethodSelector() {

        String TCID = PropertyUtil.getProperty("testName");
        if (TCID == null || TCID.isEmpty())
            return;
        for (String testName : TCID.split(",")) {
            testcaseIDList.add(testName);
        }
    }

    @Override
    public List<IMethodInstance> intercept(List<IMethodInstance> methods, ITestContext context) {
        List<IMethodInstance> updatedMethodInstance = new ArrayList<IMethodInstance>();
        for (IMethodInstance method : methods) {
            ITestNGMethod testMethod = method.getMethod();
            try {
                //&& testMethod.getGroups()[0].equals(PropertyUtil.getProperty(""))
                if (isExpectedMethod(testMethod))
                    updatedMethodInstance.add(method);
            } catch (Exception e) {
            }

        }
        return updatedMethodInstance;

    }


    /**
     * verify method whether to belong to running method
     *
     * @param method
     * @return
     */
    private boolean isExpectedMethod(ITestNGMethod method) {

        Test testMethod = method.getConstructorOrMethod().getMethod().getAnnotation(Test.class);
        String testName = testMethod.testName();

        if (!testcaseIDList.isEmpty() && !testcaseIDList.contains(testName))
            return false;
        else
            return true;
    }
}
