package com.icl.integrator.services.converters;

/**
 * Created by BigBlackBug on 3/4/14.
 */
public interface Converter<Param, Result> {

	public Result convert(Param arg);

}
