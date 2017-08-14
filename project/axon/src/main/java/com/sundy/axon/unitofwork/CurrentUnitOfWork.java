package com.sundy.axon.unitofwork;

import java.util.Deque;
import java.util.LinkedList;

/**
 * 默认入口点可以访问当前的UnitOfWork。 管理事务边界的组件可以注册和清除UnitOfWork实例，哪些组件可以使用。
 * @author Administrator
 *
 */
public abstract class CurrentUnitOfWork {

	private static final ThreadLocal<Deque<UnitOfWork>> CURRENT = new ThreadLocal<Deque<UnitOfWork>>();
	
	private CurrentUnitOfWork(){}
	
	public static boolean isStarted(){
		return CURRENT.get()!=null && !CURRENT.get().isEmpty();
	}
	
	/**
	 * 获取当前线程的工作单元，如果没有工作单元启动，那么{@link IllegalStateException} 异常会抛出
	 * @return
	 */
	public static UnitOfWork get(){
		if(isEmpty()){
			throw new IllegalStateException("No UnitOfWork is currently started for this thread.");
		}
		return CURRENT.get().peek();
	}
	
	private static boolean isEmpty(){
		Deque<UnitOfWork> unitsOfWork = CURRENT.get();
		return unitsOfWork==null || unitsOfWork.isEmpty();
	}
	
	/**
	 * 提交当前的工作单元。如果没有工作单元启动，那么{@link IllegalStateException} 异常会抛出
	 */
	public static void commit(){
		get().commit();
	}
	
	/**
	 * 绑定工作单元到当前线程。如果有其他工作单元已经绑定，那么之前的工作单元会被标记为非激活状态，直到这个传入的工作单元被清除才会再次激活
	 * @param unitOfWork
	 */
	public static void set(UnitOfWork unitOfWork){
		if (CURRENT.get() == null) {
            CURRENT.set(new LinkedList<UnitOfWork>());
        }
        CURRENT.get().push(unitOfWork);
	}
	
	/**
	 * 清除当前线程的与传入的工作单元一致的工作单元，有匹配的清除，没有的话，抛出清除不了的异常
	 * @param unitOfWork
	 */
	public static void clear(UnitOfWork unitOfWork){
		if(isStarted() && CURRENT.get().peek().equals(unitOfWork)){
			CURRENT.get().pop();
			if(CURRENT.get().isEmpty()){
				CURRENT.remove();
			}
		} else {
			throw new IllegalStateException("Could not clear this UnitOfWork. It is not the active one.");
		}
	}
	
}
