package com.sundy.axon.saga.annotation;

import java.io.Serializable;

import com.sundy.axon.common.ParameterResolverFactory;
import com.sundy.axon.common.annotation.ClasspathParameterResolverFactory;
import com.sundy.axon.domain.EventMessage;
import com.sundy.axon.domain.IdentifierFactory;
import com.sundy.axon.saga.AssociationValue;
import com.sundy.axon.saga.AssociationValues;
import com.sundy.axon.saga.Saga;

/**
 * saga接口的抽象实现类，该类可以用saga中有注解@SagaEventHandler的事件处理器来处理传入的事件
 * @author Administrator
 *
 */
public abstract class AbstractAnnotatedSaga implements Saga,Serializable {

	private static final long serialVersionUID = 2385024168304711298L;

    private final AssociationValues associationValues = new AssociationValuesImpl();
    private final String identifier;
    private transient volatile SagaMethodMessageHandlerInspector<? extends AbstractAnnotatedSaga> inspector;
    private volatile boolean isActive = true;
    private transient ParameterResolverFactory parameterResolverFactory;
	
    /**
     * 使用IdentifierFactory产生的识别码产生Saga
     */
    protected AbstractAnnotatedSaga() {
        this(IdentifierFactory.getInstance().generateIdentifier());
    }
    
    protected AbstractAnnotatedSaga(String identifier) {
        this.identifier = identifier;
        associationValues.add(new AssociationValue("sagaIdentifier", identifier));
    }
    
    public String getSagaIdentifier() {
        return identifier;
    }

    public AssociationValues getAssociationValues() {
        return associationValues;
    }

    public final void handle(EventMessage event) {
        if (isActive) {
            ensureInspectorInitialized();
            SagaMethodMessageHandler handler = inspector.findHandlerMethod(this, event);
            handler.invoke(this, event);
            if (handler.isEndingHandler()) {
                end();
            }
        }
    }
    
    private void ensureInspectorInitialized() {
        if (inspector == null) {
            if (parameterResolverFactory == null) {
                parameterResolverFactory = ClasspathParameterResolverFactory.forClass(getClass());
            }
            inspector = SagaMethodMessageHandlerInspector.getInstance(getClass(), parameterResolverFactory);
        }
    }
    
    protected void registerParameterResolverFactory(ParameterResolverFactory parameterResolverFactory) {
        this.parameterResolverFactory = parameterResolverFactory;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    protected void end() {
        isActive = false;
    }
    
    protected void associateWith(AssociationValue property) {
        associationValues.add(property);
    }
    
    protected void associateWith(String key, String value) {
        associationValues.add(new AssociationValue(key, value));
    }
    
    protected void associateWith(String key, Number value) {
        associateWith(key, value.toString());
    }
    
    protected void removeAssociationWith(AssociationValue property) {
        associationValues.remove(property);
    }
    
    protected void removeAssociationWith(String key, String value) {
        associationValues.remove(new AssociationValue(key, value));
    }
    
    protected void removeAssociationWith(String key, Number value) {
        removeAssociationWith(key, value.toString());
    }
    
    
    
    
    
    
    
    
    
    
    
}
