package com.wsss.debugger.model;

import java.io.Serializable;

/**
 * Debug请求对象
 * 封装方法调用的所有必要信息
 */
public class DebugRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    
    // Bean名称
    private String beanName;
    
    // 方法名称
    private String methodName;
    
    // 方法参数
    private Object[] arguments;
    
    // 认证密码
    private String password;
    
    public DebugRequest() {
    }
    
    public DebugRequest(String beanName, String methodName, Object[] arguments, String password) {
        this.beanName = beanName;
        this.methodName = methodName;
        this.arguments = arguments;
        this.password = password;
    }
    
    public String getBeanName() {
        return beanName;
    }
    
    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }
    
    public String getMethodName() {
        return methodName;
    }
    
    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }
    
    public Object[] getArguments() {
        return arguments;
    }
    
    public void setArguments(Object[] arguments) {
        this.arguments = arguments;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    @Override
    public String toString() {
        return "DebugRequest{" +
                "beanName='" + beanName + '\'' +
                ", methodName='" + methodName + '\'' +
                ", arguments.length=" + (arguments != null ? arguments.length : 0) +
                "}";
    }
}