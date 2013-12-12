package com.icl.integrator.task;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 04.12.13
 * Time: 14:29
 * To change this template use File | Settings | File Templates.
 */
public interface Callback<T> {

    public void execute(T arg);

}
