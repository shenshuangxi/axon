package com.sundy.axon.serializer;

import java.lang.reflect.Constructor;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.joda.time.DateTime;

import com.sundy.axon.commandhandling.GenericCommandMessage;
import com.sundy.axon.common.Assert;
import com.sundy.axon.domain.GenericDomainEventMessage;
import com.sundy.axon.domain.GenericEventMessage;
import com.sundy.axon.domain.MetaData;
import com.sundy.axon.saga.AssociationValue;
import com.sundy.axon.saga.AssociationValues;
import com.sundy.axon.saga.annotation.AbstractAnnotatedSaga;
import com.sundy.axon.saga.annotation.AssociationValuesImpl;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.collections.MapConverter;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.CannotResolveClassException;
import com.thoughtworks.xstream.mapper.Mapper;

/**
 * 基于XStream的串行器的抽象实现。 它提供了一些辅助方法和配置功能，独立于用于组织的实际格式。
 * @author Administrator
 *
 */
public abstract class AbstractXStreamSerializer implements Serializer {

	private static final Charset DEFAULT_CHARSET_NAME = Charset.forName("UTF-8");
	private final XStream xStream;
	private final Charset charset;
	private final RevisionResolver revisionResolver;
	private final ConverterFactory converterFactory;
	
	protected AbstractXStreamSerializer(XStream xStream) {
        this(xStream, new AnnotationRevisionResolver());
    }
	
	protected AbstractXStreamSerializer(XStream xStream, RevisionResolver revisionResolver) {
        this(DEFAULT_CHARSET_NAME, xStream, revisionResolver);
    }
	
	protected AbstractXStreamSerializer(Charset charset, XStream xStream) {
        this(charset, xStream, new AnnotationRevisionResolver(), new ChainingConverterFactory());
    }
	
	protected AbstractXStreamSerializer(Charset charset, XStream xStream, RevisionResolver revisionResolver) {
        this(charset, xStream, revisionResolver, new ChainingConverterFactory());
    }
	
	protected AbstractXStreamSerializer(Charset charset, XStream xStream, RevisionResolver revisionResolver,
            ConverterFactory converterFactory) {
		Assert.notNull(charset, "charset may not be null");
		Assert.notNull(xStream, "xStream may not be null");
		Assert.notNull(converterFactory, "converterFactory may not be null");
		Assert.notNull(revisionResolver, "revisionResolver may not be null");
		this.charset = charset;
		this.xStream = xStream;
		this.converterFactory = converterFactory;
		this.revisionResolver = revisionResolver;
		if (converterFactory instanceof ChainingConverterFactory) {
			registerConverters((ChainingConverterFactory) converterFactory);
		}
		xStream.registerConverter(new JodaTimeConverter());
		xStream.addImmutableType(UUID.class);
		xStream.aliasPackage("axon.domain", "org.axonframework.domain");
		xStream.aliasPackage("axon.es", "org.axonframework.eventsourcing");
		
		// Message serialization
		xStream.alias("domain-event", GenericDomainEventMessage.class);
		xStream.alias("event", GenericEventMessage.class);
		xStream.alias("command", GenericCommandMessage.class);
		
		// Configuration to enhance Saga serialization
		xStream.addDefaultImplementation(AssociationValuesImpl.class, AssociationValues.class);
		xStream.aliasField("associations", AbstractAnnotatedSaga.class, "associationValues");
		xStream.alias("association", AssociationValue.class);
		xStream.aliasField("key", AssociationValue.class, "propertyKey");
		xStream.aliasField("value", AssociationValue.class, "propertyValue");
		
		// for backward compatibility
		xStream.alias("localDateTime", DateTime.class);
		xStream.alias("dateTime", DateTime.class);
		xStream.alias("uuid", UUID.class);
		
		xStream.alias("meta-data", MetaData.class);
		xStream.registerConverter(new MetaDataConverter(xStream.getMapper()));
	}
	
	/**
	 * 注册特定于此序列化器写入的内容类型的转换器。
	 * @param converterFactory
	 */
	protected abstract void registerConverters(ChainingConverterFactory converterFactory);
	
	public <T> SerializedObject<T> serialize(Object object,
			Class<T> expectedRepresentation) {
		 T result = doSerialize(object, expectedRepresentation, xStream);
	     return new SimpleSerializedObject<T>(result, expectedRepresentation, typeForClass(object.getClass()));
	}
	
	/**
	 * 将给定类型的对象通过{@link XStream}转换为给定类型的格式
	 * @param object
	 * @param expectedFormat
	 * @param xStream
	 * @return
	 */
	protected abstract <T> T doSerialize(Object object, Class<T> expectedFormat, XStream xStream);
	
	/**
	 * 将给定类型的序列化对象通过{@link XStream}转换为对象
	 * @param serializedObject
	 * @param xStream
	 * @return
	 */
	protected abstract Object doDeserialize(SerializedObject serializedObject, XStream xStream);
	
	/**
	 * 通过转换参数获取转换器，通过转换器转换传入参数
	 * @param sourceType
	 * @param targetType
	 * @param source
	 * @return
	 */
	protected <S, T> T convert(Class<S> sourceType, Class<T> targetType, S source) {
        return getConverterFactory().getConverter(sourceType, targetType).convert(source);
    }
	
	private String revisionOf(Class<?> type) {
        return revisionResolver.revisionOf(type);
    }

	public <T> boolean canSerializeTo(Class<T> expectedRepresentation) {
		return converterFactory.hasConverter(byte[].class, expectedRepresentation);
	}

	/**
	 * {@inheritDoc}
	 */
	public <S, T> T deserialize(SerializedObject<S> serializedObject) {
		return (T) doDeserialize(serializedObject, xStream);
	}

	/**
	 * {@inheritDoc}
	 */
	public Class classForType(SerializedType type)
			throws UnknownSerializedTypeException {
		try {
            return xStream.getMapper().realClass(type.getName());
        } catch (CannotResolveClassException e) {
            throw new UnknownSerializedTypeException(type, e);
        }
	}

	public SerializedType typeForClass(Class type) {
		 return new SimpleSerializedType(typeIdentifierOf(type), revisionOf(type));
	}
	
	/**
	 * 给某个类取个别名
	 * @param name
	 * @param type
	 */
	public void addAlias(String name, Class type) {
        xStream.alias(name, type);
    }
	
	/**
	 * 给某个包取个别名
	 * @param alias
	 * @param pkgName
	 */
	public void addPackageAlias(String alias, String pkgName) {
        xStream.aliasPackage(alias, pkgName);
    }
	
	/**
	 * 给某个类成员字段取个别名
	 * @param alias
	 * @param definedIn
	 * @param fieldName
	 */
	public void addFieldAlias(String alias, Class definedIn, String fieldName) {
        xStream.aliasField(alias, definedIn, fieldName);
    }
	
	public XStream getXStream() {
        return xStream;
    }
	
	public Charset getCharset() {
        return charset;
    }

	public ConverterFactory getConverterFactory() {
		return converterFactory;
	}
	
	/**
	 * 获取给定类型的识别符
	 * @param type
	 * @return
	 */
	private String typeIdentifierOf(Class<?> type) {
        return xStream.getMapper().serializedClass(type);
    }
	
	/**
	 * XStream Converter 序列化 {@link DateTime} 为string
	 * @author Administrator
	 *
	 */
	private static final class JodaTimeConverter implements Converter {

		public boolean canConvert(Class type) {
			return type != null && DateTime.class.getPackage().equals(type.getPackage());
		}

		public void marshal(Object source, HierarchicalStreamWriter writer,
				MarshallingContext context) {
			writer.setValue(source.toString());
		}

		@SuppressWarnings("unchecked")
		public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
			try {
                Constructor constructor = context.getRequiredType().getConstructor(Object.class);
                return constructor.newInstance(reader.getValue());
            } catch (Exception e) { // NOSONAR
                throw new SerializationException(String.format(
                        "An exception occurred while deserializing a Joda Time object: %s",
                        context.getRequiredType().getSimpleName()), e);
            }
		}
	}
	
	private static final class MetaDataConverter extends MapConverter {

		public MetaDataConverter(Mapper mapper) {
			super(mapper);
		}
		
		@Override
        public boolean canConvert(Class type) {
            return MetaData.class.equals(type);
        }
		
		@Override
        public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
            MetaData metaData = (MetaData) source;
            if (!metaData.isEmpty()) {
                super.marshal(new HashMap<String, Object>(metaData), writer, context);
            }
        }
		
		@Override
        public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
            if (!reader.hasMoreChildren()) {
                return MetaData.emptyInstance();
            }
            Map<String, Object> contents = new HashMap<String, Object>();
            populateMap(reader, context, contents);
            if (contents.isEmpty()) {
                return MetaData.emptyInstance();
            } else {
                return MetaData.from(contents);
            }
        }
	}

}
