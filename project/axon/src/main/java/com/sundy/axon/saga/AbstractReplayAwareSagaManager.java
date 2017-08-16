package com.sundy.axon.saga;

import com.sundy.axon.eventhandling.replay.EventReplayUnsupportedException;
import com.sundy.axon.eventhandling.replay.ReplayAware;

/**
 * SagaManager接口的抽象实现，提供大多数SagaManager实现所需的基本功能。 提供处理事件回放的支持。
 * @author Administrator
 *
 */
public abstract class AbstractReplayAwareSagaManager implements SagaManager, ReplayAware {

	private volatile boolean replayable;
	
	/**
	 * 设置是否允许时间在sagaManager中重建。如果设置false，那么在{@link ReplayAware#beforeReplay()}前会抛出异常
	 * @param replayable
	 */
	public void setReplayable(boolean replayable) {
        this.replayable = replayable;
    }
	
	@Override
	public void beforeReplay() {
		if (!replayable) {
            throw new EventReplayUnsupportedException("This Saga Manager does not support event replays. " +
                                                      "Replaying events on its Sagas may cause data corruption.");
        }
	}

	@Override
	public void afterReplay() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onReplayFailed() {
		// TODO Auto-generated method stub

	}

}
