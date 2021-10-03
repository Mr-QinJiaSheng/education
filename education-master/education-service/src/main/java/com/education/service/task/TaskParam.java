package com.education.service.task;

import java.util.HashMap;

/**
 * 封装任务参数类
 * @author zengjintao
 * @version 1.0
 * @create_at 2020/4/13 14:46
 */
public class TaskParam extends HashMap {
    private Class<? extends TaskListener> taskListenerClass;
    private final long timestamp;
    private Object data;

    public TaskParam(Class<? extends TaskListener> taskListenerClass, long timestamp, Object data) {
        this.timestamp = timestamp;
        this.taskListenerClass = taskListenerClass;
        this.data = data;
    }

    public TaskParam(Class<? extends TaskListener> taskListenerClass, Object data) {
        this(taskListenerClass, System.currentTimeMillis(), data);
    }

    public TaskParam(Class<? extends TaskListener> taskListenerClass) {
        this(taskListenerClass, System.currentTimeMillis(), null);
    }

    public Class<? extends TaskListener> getTaskListenerClass() {
        return taskListenerClass;
    }

    public void setTaskListenerClass(Class<? extends TaskListener> taskListenerClass) {
        this.taskListenerClass = taskListenerClass;
    }
    public long getTimestamp() {
        return timestamp;
    }
    public <T> T getData() {
        return (T) data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public TaskParam() {
        this.timestamp = System.currentTimeMillis();
    }


}
