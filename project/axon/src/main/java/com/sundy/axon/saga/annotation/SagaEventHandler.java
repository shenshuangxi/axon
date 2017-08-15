package com.sundy.axon.saga.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SagaEventHandler {

	String associationProperty();
	
	String keyName() default "";
	
	Class<?> payloadType() default Void.class;
	
}
