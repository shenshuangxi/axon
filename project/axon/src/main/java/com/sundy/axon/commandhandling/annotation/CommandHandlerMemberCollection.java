package com.sundy.axon.commandhandling.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.sundy.axon.eventsourcing.annotation.AbstractAnnotatedEntity;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface CommandHandlerMemberCollection {

	/**
	 * 实体所提供的的属性名，这个属性获取的值的为这个实体的唯一识别码
	 * @return
	 */
	String entityId();
	
	/**
	 * 传入命令的载体的唯一识别码属性名称
	 * <p/>
	 * 这个名称需要符合javaBean的规范
	 * 或者可以通过 {@link org.axonframework.common.property.PropertyAccessStrategy}. 配置
	 * @return
	 */
	String commandTargetProperty();
	
	/**
	 * 注释集合中包含的实体类，默认情况下，Axon 倾向于通过泛型参数的字段来声明这个标识
	 * @return
	 */
	Class<? extends AbstractAnnotatedEntity> entityType() default AbstractAnnotatedEntity.class;
	
}
