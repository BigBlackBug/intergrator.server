package com.icl.integrator.task;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 04.12.13
 * Time: 14:18
 * To change this template use File | Settings | File Templates.
 */

public class TaskCreator<T> {

    private static long counter = 0;

    private final Log logger = LogFactory.getLog(TaskCreator.class);

    private final long taskID = counter++;

    private TaskRunnable<T> taskRunnable;

    private Descriptor<TaskCreator<T>> descriptor;

    public TaskCreator(Callable<T> callable) {
        taskRunnable = new TaskRunnable<T>(callable);
    }

    public TaskCreator setCallback(Callback<T> callback) {
        taskRunnable.setCallback(callback);
        return this;
    }

    public TaskCreator setDescriptor(Descriptor<TaskCreator<T>> descriptor) {
        this.descriptor = descriptor;
        return this;
    }

    public String getTaskDescription(){
        if(descriptor == null){
            return super.toString();
        }
        return descriptor.describe(this);
    }

    @Override
    public String toString() {
        return getTaskDescription();
    }

    public long getTaskID() {
        return taskID;
    }

    public <Y extends Exception> TaskCreator addExceptionHandler
            (Callback<Y> exceptionCallback,
             Class<Y> exceptionClass) {
        taskRunnable.addExceptionCallback(exceptionCallback, exceptionClass);
        return this;
    }

    public TaskCreator setDefaultExceptionHandler(
            Callback<Exception> exceptionCallback) {
        taskRunnable.setDefaultExceptionHandler(exceptionCallback);
        return this;
    }

    public Runnable create() {
        return taskRunnable;
    }

    public class TaskRunnable<T> implements Runnable {

        private Callback<Exception> defaultExceptionHandler =
            new Callback<Exception>() {
                @Override
                public void execute(Exception exception) {
                    logger.error("An exception has occurred", exception);
                }
            };

        private Callback<T> callback;

        private Callable<T> runnable;

        private Map<Class<? extends Exception>, Callback<? extends Exception>>
                exceptionHandlerMap = new HashMap<>();

        TaskRunnable(Callable<T> runnable) {
            this.runnable = runnable;
        }

        public <Y extends Exception>
        void addExceptionCallback(Callback<Y> exceptionCallback,
                                  Class<Y> exceptionClass) {
            exceptionHandlerMap.put(exceptionClass, exceptionCallback);
        }

        public void setDefaultExceptionHandler(
                Callback<Exception> defaultExceptionHandler) {
            this.defaultExceptionHandler = defaultExceptionHandler;
        }

        @Override
        public void run() {
            T result = null;
            try {
                result = runnable.call();
            } catch (Exception e) {
                Class<?> excClass = e.getClass();
                Callback exceptionCallback = null;
                do {
                    exceptionCallback =
                            exceptionHandlerMap.get(excClass);
                    if (exceptionCallback != null) {
                       break;
                    }
                    excClass = excClass.getSuperclass();
                } while (!excClass.equals(Exception.class));
                if(exceptionCallback == null){
                    exceptionCallback = defaultExceptionHandler;
                }
                exceptionCallback.execute(e);
                return;
            }
            if (callback != null) {
                callback.execute(result);
            }
        }

        public void setCallback(Callback<T> callback) {
            this.callback = callback;
        }
    }

}