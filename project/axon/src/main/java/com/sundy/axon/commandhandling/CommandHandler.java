package com.sundy.axon.commandhandling;

import com.sundy.axon.domain.CommandMessage;
import com.sundy.axon.unitofwork.UnitOfWork;

/**
 * 标记一个实例可以处理命令，并需要将自己订阅到命令总线上，以便于自己接收命令
 * @author Administrator
 *
 * @param <T> 命令的一种类型， 声明后该命令处理器值处理该给定的命令
 */
public interface CommandHandler<T> {

	/**
	 * 处理给定的命令
	 * @param commandMessage
	 * @param unitOfWork
	 * @return
	 * @throws Throwable
	 */
	Object handle(CommandMessage<T> commandMessage, UnitOfWork unitOfWork) throws Throwable;
	
}
