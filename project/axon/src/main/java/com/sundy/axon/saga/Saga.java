package com.sundy.axon.saga;

import com.sundy.axon.domain.EventMessage;

/**
 * 描述Saga实现的接口。 Sagas是处理事件并可能产生新命令或具有其他副作用的实例。 通常，Sagas用于管理长时间运行的业务交易。
 * 一般情况下，saga用于管理长时间运行的业务交易
 * <p/>
 * 一个类型的saga可能存在多个实例，每一个saga可能管理不同的交易。saga需要关联不同的主题以便接收特定的事件。
 * 关联属性可以通过AssociationValues来管理。比如 要将saga与ID为1234的订单相关联，此saga需要一个与“orderId”和值“1234”的关联值。
 * @author Administrator
 *
 */
public interface Saga {

	/**
	 * 返回saga的唯一标识符
	 * @return
	 */
	String getSagaIdentifier();
	
	/**
	 * 返回saga的关联属性，该属性是只读的
	 * @return
	 */
	AssociationValues getAssociationValues();
	
	/**
	 * 处理给定的事件，实际的处理过程依靠该接口的具体实现。
	 * <p/>
	 * 具体的实例不推荐抛出异常
	 * @param event
	 */
	void handle(EventMessage event);
	
	/**
	 * 判断saga是否为活跃状态。saga保持活跃状态直到该saga的生命周期结束
	 * @return
	 */
	boolean isActive();
	
}
