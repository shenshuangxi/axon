package com.sundy.axon.eventhandling.replay;

/**
 * 用于重建事件
 * @author Administrator
 *
 */
public interface ReplayAware {

	/**
	 * 事件重建前调用
	 */
	void beforeReplay();
	
	/**
	 * 事件重建后调用
	 */
	void afterReplay();
	
	/**
	 * 事件重建失败后调用
	 */
	void onReplayFailed();
	
}
