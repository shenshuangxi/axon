package com.sundy.axon.domain;

import java.util.Map;

import org.joda.time.DateTime;

/**
 * 消息事件接口的通用实例，该实例仅仅只有元数据和载体的引用
 * @author Administrator
 *
 * @param <T>
 */
public class GenericEventMessage<T> extends GenericMessage<T> implements EventMessage<T> {

	private static final long serialVersionUID = 772943442694177550L;
	private final DateTime timestamp;
	
	public GenericEventMessage(T payload) {
		this(payload, MetaData.emptyInstance());
	}

	public GenericEventMessage(String identifier, DateTime timestamp, T payload, Map<String, ?> metaData) {
		super(identifier,payload,metaData);
		this.timestamp = timestamp;
	}
	
	public GenericEventMessage(T payload, Map<String, ?> metaData) {
		super(payload, metaData);
		this.timestamp = new DateTime();
	}
	
	private GenericEventMessage(GenericEventMessage<T> original, Map<String, ?> metaData) {
        super(original.getIdentifier(), original.getPayload(), metaData);
        this.timestamp = original.getTimeStamp();
    }
	

	public static <T> EventMessage<T> asEventMessage(Object event){
		if(EventMessage.class.isInstance(event)){
			return (EventMessage<T>) event;
		}else if(event instanceof Message){
			Message message = (Message) event;
			return new GenericEventMessage<T>((T)message.getPayload(), message.getMetaData());
		}
		return new GenericEventMessage<T>((T)event);
	}
	
	
	
	public DateTime getTimeStamp() {
		return timestamp;
	}

	public EventMessage<T> withMetaData(Map<String, ?> metaData) {
		if(getMetaData().equals(metaData)){
			return this;
		}
		return new GenericEventMessage<T>(this, metaData);
	}

	public EventMessage<T> andMetaData(Map<String, ?> metaData) {
		if(metaData.isEmpty()){
			return this;
		}
		return new GenericEventMessage<T>(this, getMetaData().mergedWith(metaData));
	}
	
	@Override
    public String toString() {
        return String.format("GenericEventMessage[%s]", getPayload().toString());
    }

}
