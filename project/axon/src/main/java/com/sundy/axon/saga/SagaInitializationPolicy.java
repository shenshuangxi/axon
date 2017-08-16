package com.sundy.axon.saga;

/**
 * 描述创建saga的条件，如哪些资源在初始化的时候需要，使用什么策略什么策略来创建
 * @author Administrator
 *
 */
public class SagaInitializationPolicy {

	/**
	 * 初始化一个空的
	 */
	public static final SagaInitializationPolicy NONE = new SagaInitializationPolicy(SagaCreationPolicy.NONE, null);
	
	private final SagaCreationPolicy sagaCreationPolicy;
	private final AssociationValue initialAssociationValue;
	
	public SagaInitializationPolicy(SagaCreationPolicy sagaCreationPolicy, AssociationValue initialAssociationValue) {
        this.sagaCreationPolicy = sagaCreationPolicy;
        this.initialAssociationValue = initialAssociationValue;
    }

	public SagaCreationPolicy getSagaCreationPolicy() {
		return sagaCreationPolicy;
	}

	public AssociationValue getInitialAssociationValue() {
		return initialAssociationValue;
	}
	
	
	
}
