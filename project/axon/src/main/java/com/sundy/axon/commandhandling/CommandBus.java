package com.sundy.axon.commandhandling;

import com.sundy.axon.domain.CommandMessage;

/**
 * 该接口用于描述 分发命令如何到达所匹配的命令处理器。命令处理器都可以订阅或取消订阅者一个类型的命令在这个命令总线上，
 * 任何时候都只存在一个命令处理器对应一个类型的命令
 * @author Administrator
 *
 */
public interface CommandBus {

	/**
	 * 分发一条命令到已订阅有该命令的命令处理器的命令总线，处理后立即返回，没有反馈这个分发的状态
	 * @param command
	 */
	void dispatch(CommandMessage<?> command);
	
	/**
	 * 分发一条命令到已订阅有该命令的命令处理器的命令总线，处理完成后，调用反馈的那种方法执行，取决于处理的结果
	 * <p/>
	 * 当该方法返回时，CommandBus实现提供的唯一保证是该命令已成功接收。 强烈推荐实现在从该方法调用返回之前执行命令的基本验证。
	 * <p/>
	 * 实现类必须在命令被分发之前启动一个工作单元(UnitOfWork) 并在命令执行成功或失败后  提交commit or 回滚rollback
	 * @param command
	 */
	<R> void dispatch(CommandMessage<?> command, CommandCallback<R> callback);
	
	/**
	 * 将命令处理器网总线上订阅一种命令
	 * <p/>
	 * 如果该类型的订阅已存在，行为未定义， 实现类需要抛出异常，并拒绝重复订阅，或者 决定保存原有的还是新的处理器存在在命令总线上
	 * @param commandName
	 * @param handler
	 */
	<C> void subscribe(String commandName, CommandHandler<? super C> handler);
	
	/**
	 * 将一个命令处理器从总线上移除掉，移除后该命令处理器不再接收处理新的命令
	 * @param commandName
	 * @param handler
	 */
	<C> void unsubscribe(String commandName, CommandHandler<? super C> handler);
}
