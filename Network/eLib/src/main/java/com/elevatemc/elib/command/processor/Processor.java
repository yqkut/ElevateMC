package com.elevatemc.elib.command.processor;

@FunctionalInterface
public interface Processor<T, R> {

    R process(T var1);

}