package com.sundy.axon.serializer;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 该注解用于向序列化对象添加版本信息。识别码的版本可以有助于upcaster判断师傅需要处理某个已序列化的事件，
 * 一般来说，该版本号会在事件状态改变时做出修改
 * <p/>
 * 即使该版本号是可以继承的，还是严格建议你在实现类上的实际使用。这样会更好的便于保持对upcasters的实时更新
 * @author Administrator
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Inherited
public @interface Revision {

	/**
	 * 当前对象的识别码版本号
	 * @return
	 */
	String value();
	
}
