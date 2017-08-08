package com.sundy.axon.common;

import java.lang.annotation.Annotation;

/**
 * 参数解析器工厂，生成有能力解析消息成参数给注解方法的实例，
 * <p/>
 * 该接口的实现类，{@link ClasspathParameterResolverFactory} 可以让开发者通过服务类的类加载截止自定义参数解析工厂，
 * 可以防止一个文件名为<code>org.axonframework.common.annotation.ParameterResolverFactory</code>到<code>META-INF/services</code>的文件夹中
 * 在这个文件中，可以写入自定义的解析工厂的全路径
 * <p/>
 * 该工厂的实现类必须是public，非抽象的。
 * @author Administrator
 *
 */
public interface ParameterResolverFactory {

	/**
	 * 如果可以，会根据提供的参数类型生成一个解析指定消息的解析器
	 * @param memberAnnotations 	放置于命令处理器上的注释
	 * @param parameterType			解析后的参数类型
	 * @param parameterAnnotations	参数类型的注解
	 * @return
	 */
	ParameterResolver createInstance(Annotation[] memberAnnotations, Class<?> parameterType, Annotation[] parameterAnnotations);
	
}
