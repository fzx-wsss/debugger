package com.wsss.debugger.model;

import java.io.Serializable;

/**
 * Debug响应对象
 * 封装方法调用的返回结果和相关信息
 */
public class DebugResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    
    // 调用是否成功
    private boolean success;
    
    // 返回结果数据
    private Object result;
    
    // 错误信息（如果有）
    private String errorMessage;
    
    // 异常信息（如果有）
    private String exceptionClass;
    
    // 方法执行时间（毫秒）
    private long executionTime;
    
    // 结果类型
    private String resultType;
    
    public DebugResponse() {
    }
    
    public DebugResponse(boolean success) {
        this.success = success;
    }
    
    public DebugResponse(Object result) {
        this.success = true;
        this.result = result;
        if (result != null) {
            this.resultType = result.getClass().getName();
        }
    }
    
    public DebugResponse(String errorMessage, String exceptionClass) {
        this.success = false;
        this.errorMessage = errorMessage;
        this.exceptionClass = exceptionClass;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public Object getResult() {
        return result;
    }
    
    public void setResult(Object result) {
        this.result = result;
        if (result != null) {
            this.resultType = result.getClass().getName();
        }
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    public String getExceptionClass() {
        return exceptionClass;
    }
    
    public void setExceptionClass(String exceptionClass) {
        this.exceptionClass = exceptionClass;
    }
    
    public long getExecutionTime() {
        return executionTime;
    }
    
    public void setExecutionTime(long executionTime) {
        this.executionTime = executionTime;
    }
    
    public String getResultType() {
        return resultType;
    }
    
    public void setResultType(String resultType) {
        this.resultType = resultType;
    }
    
    @Override
    public String toString() {
        return "DebugResponse{" +
                "success=" + success +
                ", resultType='" + resultType + '\'' +
                ", executionTime=" + executionTime + "ms" +
                (errorMessage != null ? ", errorMessage='" + errorMessage + '\'' : "") +
                "}";
    }
}