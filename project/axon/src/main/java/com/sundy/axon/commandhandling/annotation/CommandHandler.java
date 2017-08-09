package com.sundy.axon.commandhandling.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 该注解用于标记一个方法是否为命令处理器，使用 {@link AnnotationCommandHandlerAdapter} 将该方法订阅到命令总线
 * <p/>
 * 该注解也可以用用于聚合的的成员上，比如{@link AggregateAnnotationCommandHandler} 可以订阅到命令总线。如果这个注解放在聚合的构造方法上，那么将会产生一个新的聚合。如果没有根实体的聚合，那么它的成员字段上一定
 * 会被{@link CommandHandlingMember} 或 {@link CommandHandlingMember} 表示这个注解没有用于非根实体的构造方法上
 * <p/>
 * 该注解方法的第一个参数，就是命令，第二个参数可能是UnitOfWork工作单元，如果有这个参数，那么工作单元必须是可用的
 * @author Administrator
 *
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
public @interface CommandHandler {

	String commandName() default "";
	
}
