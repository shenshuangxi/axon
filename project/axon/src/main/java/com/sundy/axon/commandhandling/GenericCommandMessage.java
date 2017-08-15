package com.sundy.axon.commandhandling;

import java.util.Map;

import com.sundy.axon.domain.CommandMessage;
import com.sundy.axon.domain.IdentifierFactory;
import com.sundy.axon.domain.MetaData;

/**
 * CommandMessage 的实现类，用所有的属性作为其构造方法的参数
 * @author Administrator
 *
 */
public class GenericCommandMessage<T> implements CommandMessage<T> {

	private static final long serialVersionUID = 8754588074137370013L;

    private final String identifier;
    private final String commandName;
    private final T payload;
    private final MetaData metaData;
    
    public static CommandMessage asCommandMessage(Object command){
    	if(CommandMessage.class.isInstance(command)){
    		return (CommandMessage) command;
    	}
    	return new GenericCommandMessage<Object>(command);
    }
    
    public GenericCommandMessage(T payload) {
        this(payload, MetaData.emptyInstance());
    }
    
    public GenericCommandMessage(T payload, Map<String, ?> newMetaData) {
        this(payload.getClass().getName(), payload, newMetaData);
    }
    
    public GenericCommandMessage(String commandName, T payload, Map<String, ?> newMetaData) {
        this.commandName = commandName;
        this.payload = payload;
        this.metaData = MetaData.from(newMetaData);
        this.identifier = IdentifierFactory.getInstance().generateIdentifier();
    }
    
    public GenericCommandMessage(String identifier, String commandName, T payload, Map<String, ?> newMetaData) {
        this.identifier = identifier;
        this.commandName = commandName;
        this.payload = payload;
        this.metaData = MetaData.from(newMetaData);
    }
    
    protected GenericCommandMessage(GenericCommandMessage<T> original, Map<String, ?> metaData) {
        this.identifier = original.getIdentifier();
        this.commandName = original.getCommandName();
        this.payload = original.getPayload();
        this.metaData = MetaData.from(metaData);
    }
	
    public String getCommandName() {
        return commandName;
    }

    public MetaData getMetaData() {
        return metaData;
    }

    public T getPayload() {
        return payload;
    }

    public Class getPayloadType() {
        return payload.getClass();
    }

    public String getIdentifier() {
        return identifier;
    }

    public GenericCommandMessage<T> withMetaData(Map<String, ?> newMetaData) {
        if (getMetaData().equals(newMetaData)) {
            return this;
        }
        return new GenericCommandMessage<T>(this, newMetaData);
    }

    public GenericCommandMessage<T> andMetaData(Map<String, ?> additionalMetaData) {
        if (additionalMetaData.isEmpty()) {
            return this;
        }
        return new GenericCommandMessage<T>(this, metaData.mergedWith(additionalMetaData));
    }

}
