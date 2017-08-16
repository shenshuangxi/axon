package com.sundy.axon.saga;

import com.sundy.axon.domain.EventMessage;
import com.sundy.axon.eventhandling.EventListenerProxy;

/**
 * 接口用于管理一个或多个类型的saga，sagaManager是一个基础对象用于将发布的事件关联到saga，基于这些发布的事件，sagaManager也能管理saga的生命周期
 * <p/>
 * sagaManager必须是线程安全的，该接口实例可以提供锁来实现访问saga也是线程安全的
 * @author Administrator
 *
 */
public interface SagaManager extends EventListenerProxy {

	/**
	 * 将事件消息传递给所有关联到该消息中属性的saga实例
	 */
	@Override
    void handle(EventMessage event);
	
}
