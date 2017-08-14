package com.sundy.axon.serializer;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.ServiceLoader;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sundy.axon.upcasting.Upcaster;

/**
 * 转换器工厂的实现类，该类能将类型转换器合并成一条链，从而可以将数据从一种类型转换为另一种类型。以防止单一转换器不能完成转换任务
 * <p/>
 * 该实现类可以通过扫描<code>/META-INF/services/org.axonframework.serializer.ContentTypeConverter</code>文件，自动发现类型转换器，(该文件必须包含这些类的全路径)
 * @author Administrator
 *
 */
public class ChainingConverterFactory implements ConverterFactory {

	private static final Logger logger = LoggerFactory.getLogger(ChainingConverterFactory.class);
	private final List<ContentTypeConverter<?, ?>> converters = new CopyOnWriteArrayList<ContentTypeConverter<?,?>>();
	
	/**
	 * 自动扫描<code>/META-INF/services/org.axonframework.serializer.ContentTypeConverter</code> 并将转换类加载经转换器列表
	 * <p/>
	 * 该方法是线程安全的加载
	 */
	public ChainingConverterFactory(){
		ServiceLoader<ContentTypeConverter> converterLoader = ServiceLoader.load(ContentTypeConverter.class);
		for(ContentTypeConverter converter : converterLoader){
			converters.add(converter);
		}
	}

	public <S, T> boolean hasConverter(Class<S> sourceContentType,
			Class<T> targetContentType) {
		if(sourceContentType.equals(targetContentType)){
			return true;
		}
		for(ContentTypeConverter converter : converters){
			if(canConvert(converter,sourceContentType, targetContentType)){
				return true;
			}
		}
		return ChainedConverter.canConvert(sourceContentType,targetContentType,converters);
	}
	
	private <S, T> boolean canConvert(ContentTypeConverter converter,Class<S> sourceContentType, Class<T> targetContentType){
		try {
			if(converter.expectedSourceType().isAssignableFrom(sourceContentType) && converter.targetType().isAssignableFrom(targetContentType)){
				return true;
			}
			converter.targetType();//调用该方法确认该目标类型在类路径中
		} catch (Exception e) {
			logger.info("ContentTypeConverter [{}] is ignored. It seems to rely on a class that is "
                    + "not available in the class loader: {}", converter, e.getMessage());
			converters.remove(converter);
		}
		return false;
	}

	public <S, T> ContentTypeConverter<S, T> getConverter(
			Class<S> sourceContentType, Class<T> targetContentType) {
		if (sourceContentType.equals(targetContentType)) {
            return new NoConversion(sourceContentType);
        }
		for(ContentTypeConverter converter : converters){
			if(canConvert(converter,sourceContentType, targetContentType)){
				return converter;
			}
		}
		ChainedConverter<S, T> converter = ChainedConverter.calculateChain(sourceContentType, targetContentType,
                converters);
		converters.add(0, converter);
		return converter;
	}
	
	/**
	 * 在此工厂注册给定的转换器。 在给定输入和输出类型找到合适的转换器时，将首先检查最后注册的转换器。
	 * @param converter 注册的转换器
	 */
	public void registerConverter(ContentTypeConverter converter){
		converters.add(0, converter);
	}
	
	/**
	 * 只有在这样一个转换器的初始化可能的情况下，才能将这个转换器类型转换成这个工厂。 在类路径上检查预期的源类型和目标类型类的可用性。 
	 * 与registerConverter（ContentTypeConverter）相反，此方法允许注册转换器的潜在不安全（按类依赖关系）。
	 * @param converterType
	 */
	public void registerConverter(Class<? extends ContentTypeConverter> converterType){
		try {
			ContentTypeConverter converter = converterType.getConstructor().newInstance();
			converter.targetType();
			converter.expectedSourceType();
			registerConverter(converter);
		} catch (Exception e) {
			logger.warn("An exception occurred while trying to initialize a [{}].", converterType.getName(), e);
		}catch (NoClassDefFoundError e) {
            logger.info("ContentTypeConverter of type [{}] is ignored. It seems to rely on a class that is "
                    + "not available in the class loader: {}", converterType, e.getMessage());
		}
	}
	
	/**
	 * 需要属性方法的依赖注入框架的Setter。 该方法与在附加转换器的给定列表中为每个转换器调用registerConverter（ContentTypeConverter）相同。
	 * @param additionalConverters
	 */
	public void setAdditionalConverters(List<ContentTypeConverter> additionalConverters) {
        for (ContentTypeConverter converter : additionalConverters) {
            registerConverter(converter);
        }
    }
	
	private static class NoConversion<T> implements ContentTypeConverter<T, T>{
		private final Class<T> type;

		public NoConversion(Class<T> type) {
			this.type = type;
		}

		public Class<T> expectedSourceType() {
			return type;
		}

		public Class<T> targetType() {
			return type;
		}

		public SerializedObject<T> convert(SerializedObject<T> original) {
			return original;
		}

		public T convert(T original) {
			return original;
		}

		
		
		
	}
	
	

}
