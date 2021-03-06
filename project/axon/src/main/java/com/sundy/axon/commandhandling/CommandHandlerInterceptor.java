package com.sundy.axon.commandhandling;

import com.sundy.axon.domain.CommandMessage;
import com.sundy.axon.unitofwork.UnitOfWork;

/**
 * 工作流接口，允许自定义命令处理器执行链，一个命令拦截器可以添加自定义行为的命令处理执行链到 命令拦截器之前或之后
 * @author Administrator
 *
 */
public interface CommandHandlerInterceptor {

	/**
	 * 当一个命令通过一条已被CommandHandlerInterceptor声明的命令总线分发时，该方法将被执行，命令已及命令的上下文信息可以在unitOfWork中找到
	 * <p/>
	 * 拦截器主要是通过{@link InterceptorChain}上的方法process来执行的
	 * <p/>
	 * 拦截器收集到的所有信息，都可能附加到unitOfWork，这些信息对于CommandCallback可能会有用
	 * <p/>
	 * 拦截器建议不要改变命令处理的结果，因为分发组件可能期望得到一个确定类型的结果
	 * @param commandMessage 将被分发的命令
	 * @param unitOfWork	工作单元
	 * @param interceptorChain	拦截链，允许拦截器处理分发进程
	 * @return
	 */
	Object handle(CommandMessage<?> commandMessage, UnitOfWork unitOfWork, InterceptorChain interceptorChain)throws Throwable;
	
}
