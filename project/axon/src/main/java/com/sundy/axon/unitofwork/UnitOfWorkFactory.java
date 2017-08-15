package com.sundy.axon.unitofwork;


/**
 * UnitOfWorkFactory接口用于获取UnitOfWork实例以管理命令处理过程中的活动。
 * <p/>
 * 此工厂返回的所有UnitOfWork实例已启动。 完成后，调用者有责任在这些实例上调用提交或回滚。
 * @author Administrator
 *
 */
public interface UnitOfWorkFactory {

	/**
	 * 返回一个UnitOfWork实例，该实例的中的方法{@link UnitOfWork#isStarted()}返回true
	 * @return 返回一个已启动的工作单元实例。
	 */
	UnitOfWork createUnitOfWork();
	
}
