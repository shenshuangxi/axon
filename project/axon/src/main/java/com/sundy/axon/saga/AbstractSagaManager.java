package com.sundy.axon.saga;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sundy.axon.common.Assert;
import com.sundy.axon.common.Subscribable;
import com.sundy.axon.common.lock.IdentifierBasedLock;
import com.sundy.axon.correlation.CorrelationDataHolder;
import com.sundy.axon.correlation.CorrelationDataProvider;
import com.sundy.axon.correlation.MultiCorrelationDataProvider;
import com.sundy.axon.correlation.SimpleCorrelationDataProvider;
import com.sundy.axon.domain.EventMessage;
import com.sundy.axon.eventhandling.EventBus;

public abstract class AbstractSagaManager extends AbstractReplayAwareSagaManager implements Subscribable {

	private static final Logger logger = LoggerFactory.getLogger(AbstractReplayAwareSagaManager.class);
	
	private final EventBus eventBus;
    private final SagaRepository sagaRepository;
    private final SagaFactory sagaFactory;
    private final Class<? extends Saga>[] sagaTypes;
    private final IdentifierBasedLock lock = new IdentifierBasedLock();
    private final Map<String, Saga> sagasInCreation = new ConcurrentHashMap<String, Saga>();
    private volatile boolean suppressExceptions = true;
    private volatile boolean synchronizeSagaAccess = true;
    private CorrelationDataProvider<? super EventMessage> correlationDataProvider = new SimpleCorrelationDataProvider();
    
    public AbstractSagaManager(EventBus eventBus, SagaRepository sagaRepository, SagaFactory sagaFactory,
            Class<? extends Saga>... sagaTypes) {
		Assert.notNull(eventBus, "eventBus may not be null");
		Assert.notNull(sagaRepository, "sagaRepository may not be null");
		Assert.notNull(sagaFactory, "sagaFactory may not be null");
		this.eventBus = eventBus;
		this.sagaRepository = sagaRepository;
		this.sagaFactory = sagaFactory;
		this.sagaTypes = sagaTypes;
	}
    
    public AbstractSagaManager(SagaRepository sagaRepository, SagaFactory sagaFactory,
            Class<? extends Saga>... sagaTypes) {
		Assert.notNull(sagaRepository, "sagaRepository may not be null");
		Assert.notNull(sagaFactory, "sagaFactory may not be null");
		this.eventBus = null;
		this.sagaRepository = sagaRepository;
		this.sagaFactory = sagaFactory;
		this.sagaTypes = sagaTypes;
	}
	
	@Override
	public void handle(EventMessage event) {
		
		for(Class<? extends Saga> sagaType : sagaTypes){
			Collection<AssociationValue> associationValues = extractAssociationValues(sagaType,event);
			if(associationValues!=null && !associationValues.isEmpty()){
				boolean sagaOfTypeInvoked = invokeExistingSagas(event,sagaType,associationValues);
				
			}
		}

	}

	private boolean invokeExistingSagas(EventMessage event, Class<? extends Saga> sagaType, Collection<AssociationValue> associationValues) {
		Set<String> sagas = new TreeSet<String>();
		for(AssociationValue associationValue : associationValues){
			sagas.addAll(sagaRepository.find(sagaType, associationValue));
		}
		for(Saga saga : sagasInCreation.values()){
			if(sagaType.isInstance(saga) && containsAny(saga.getAssociationValues(),associationValues)){
				sagas.add(saga.getSagaIdentifier());
			}
		}
		boolean sagaOfTypeInvoked = false;
		for(final String sagaId : sagas){
			if(synchronizeSagaAccess){
				lock.obtainLock(sagaId);
				Saga invokedSaga = null;
				invokedSaga = loadAndInvoke(event, sagaId, associationValues);
				if(invokedSaga != null){
					sagaOfTypeInvoked = true;
				}
			}
		}
		return false;
	}

	private Saga loadAndInvoke(EventMessage event, String sagaId, Collection<AssociationValue> associationValues) {
		Saga saga = sagasInCreation.get(sagaId);
		if(saga == null){
			saga = sagaRepository.load(sagaId);
		}
		if(sagaId==null || !saga.isActive() || !containsAny(saga.getAssociationValues(), associationValues)){
			return null;
		}
		preProcessSaga(saga);
		try {
			doInvokeSaga(event,saga);
		} finally{
			commit(saga);
		}
		return saga;
	}

	protected void commit(Saga saga) {
		sagaRepository.commit(saga);
	}

	private void doInvokeSaga(EventMessage event, Saga saga) {
		try {
			CorrelationDataHolder.setCorrelationData(correlationDataProvider.correlationDataFor(event));
			saga.handle(event);
		} catch (RuntimeException e) {
            if (suppressExceptions) {
                logger.error(String.format("An exception occurred while a Saga [%s] was handling an Event [%s]:",
                                saga.getClass().getSimpleName(),
                                event.getPayloadType().getSimpleName()),
                        e);
            } else {
                throw e;
            }
        } finally {
            CorrelationDataHolder.clear();
        }
		
	}

	/**
	 * 执行新创建或从存储库加载的sagas的预处理。 在调用saga实例本身之前调用此方法。
	 * @param saga
	 */
	protected void preProcessSaga(Saga saga) {
		
	}

	private boolean containsAny(AssociationValues associationValues, Collection<AssociationValue> toFind) {
		for(AssociationValue valueToFind : toFind){
			if(associationValues.contains(valueToFind)){
				return true;
			}
		}
		return false;
	}

	protected abstract Collection<AssociationValue> extractAssociationValues(Class<? extends Saga> sagaType, EventMessage event) ;
	
	protected abstract SagaInitializationPolicy getSagaCreationPolicy(Class<? extends Saga> sagaType, EventMessage event);

	@Override
	public Class<?> getTargetType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void unsubscribe() {
		if (eventBus != null) {
            eventBus.unsubscribe(this);
        }
	}

	@Override
	public void subscribe() {
		if (eventBus != null) {
            eventBus.subscribe(this);
        }
	}

	public void setSuppressExceptions(boolean suppressExceptions) {
		this.suppressExceptions = suppressExceptions;
	}

	public void setSynchronizeSagaAccess(boolean synchronizeSagaAccess) {
		this.synchronizeSagaAccess = synchronizeSagaAccess;
	}

	public void setCorrelationDataProvider(
			CorrelationDataProvider<? super EventMessage> correlationDataProvider) {
		this.correlationDataProvider = correlationDataProvider;
	}
	
	public void setCorrelationDataProviders(
            List<? extends CorrelationDataProvider<? super EventMessage>> correlationDataProviders) {
        this.correlationDataProvider = new MultiCorrelationDataProvider<EventMessage>(correlationDataProviders);
    }
	
	public Set<Class<? extends Saga>> getManagedSagaTypes() {
        return new LinkedHashSet<Class<? extends Saga>>(Arrays.asList(sagaTypes));
    }

}
