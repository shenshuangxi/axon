package com.sundy.axon.common.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;

public abstract class AbstractAnnotatedHandlerDefinition<T extends Annotation> implements
		HandlerDefinition<AccessibleObject> {

	private final Class<T> annotationType;

	protected AbstractAnnotatedHandlerDefinition(Class<T> annotationType) {
		this.annotationType = annotationType;
	}

	public Class<T> getAnnotationType() {
		return annotationType;
	}

	public boolean isMessageHandler(AccessibleObject member) {
		return member.isAnnotationPresent(annotationType);
	}

	public Class<?> resolvePayloadFor(AccessibleObject member) {
		T annotation = member.getAnnotation(annotationType);
		Class<?> definedPayload = null;
		if(annotation != null){
			definedPayload = getDefinedPayload(annotation);
			if(definedPayload == Void.class){
				return null;
			}
		}
		return definedPayload;
	}

	/**
	 * 获取类成员上的注解配置的暴露的消息载体，如果载体类型为Void(因为注解上不允许为null的值) 那么可以认为 没有暴露载体配置 
	 * @param annotation
	 * @return
	 */
	protected abstract Class<?> getDefinedPayload(T annotation);
	
	@Override
    public String toString() {
        return "AnnotatedHandler{" + annotationType + '}';
    }
	
}
