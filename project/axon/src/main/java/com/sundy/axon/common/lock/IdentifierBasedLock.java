package com.sundy.axon.common.lock;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 该锁允许对个线程持有一把锁，直到持有锁的识别码不一致。如果锁释放掉了，那么被锁的实体也会被自动清理掉
 * <p/>
 * 该锁是可以重入的，意味着每个线程可以多次持有一把相同的锁。当锁已经被释放了很多次时，该锁只会被释放给其他线程。
 * <p/>
 * 该锁可以确保线程可以安全的访问多个对象 比如 聚合和saga
 * @author Administrator
 *
 */
public class IdentifierBasedLock {

	private static final Set<IdentifierBasedLock> INSTANCES = Collections.newSetFromMap(Collections.synchronizedMap(new WeakHashMap<IdentifierBasedLock, Boolean>()));
	
	private final ConcurrentHashMap<String, DisposableLock> locks = new ConcurrentHashMap<String, DisposableLock>();
	
	public IdentifierBasedLock() {
        INSTANCES.add(this);
    }
	
	private static Set<Thread> threadsWaitingForMyLocks(Thread owner) {
        return threadsWaitingForMyLocks(owner, INSTANCES);
    }
	
	private static Set<Thread> threadsWaitingForMyLocks(Thread owner, Set<IdentifierBasedLock> locksInUse){
		Set<Thread> waitingThreads = new HashSet<Thread>();
		for(IdentifierBasedLock lock : locksInUse){
			for(DisposableLock disposableLock : lock.locks.values()){
				if(disposableLock.isHeldBy(owner)){
					final Collection<Thread> c = disposableLock.queuedThreads();
					for(Thread thread : c){
						if(waitingThreads.add(thread)){
							waitingThreads.addAll(threadsWaitingForMyLocks(thread, locksInUse));
						}
					}
				}
			}
		}
		return waitingThreads;
	}
	
	public boolean hasLock(String identifier){
		return isLockAvailableFor(identifier) && lockFor(identifier).isHeldByCurrentThread();
	}
	
	/**
	 * 根据给定的识别码，该方法会阻塞直到成功获取到锁
	 * <p/>
	 * 注意:在获取锁的过程中发生了异常，那么这个锁有可能获取不到
	 * @param identifier
	 */
	public void obtainLock(String identifier){
		boolean lockObtained = false;
		while(!lockObtained){
			DisposableLock lock = lockFor(identifier);
			lockObtained = lock.lock();
			if(!lockObtained){
				locks.remove(identifier, lock);
			}
		}
	}
	
	public void releaseLock(String identifier){
		if(!locks.containsKey(identifier)){
			throw new IllegalLockUsageException("No lock for this identifier was ever obtained");
		}
		DisposableLock lock = lockFor(identifier);
		lock.unlock(identifier);
	}
	
	private boolean isLockAvailableFor(String identifier){
		return locks.containsKey(identifier);
	}
	
	private DisposableLock lockFor(String identifier){
		DisposableLock lock = locks.get(identifier);
		while(lock == null){
			locks.putIfAbsent(identifier, new DisposableLock());
			lock = locks.get(identifier);
		}
		return lock;
	}
	
	
	private final class DisposableLock {
		private final PubliclyOwnedReentrantLock lock;
		
		private boolean isClosed = false;
		
		private DisposableLock(){
			this.lock = new PubliclyOwnedReentrantLock();
		}
		
		private boolean isHeldByCurrentThread(){
			return lock.isHeldByCurrentThread();
		}
		
		private void unlock(String identifier){
			try {
				lock.unlock();
			} finally{
				disposeIfUnused(identifier);
			}
		}
		
		private void disposeIfUnused(String identifier) {
			try {
				if(lock.tryLock()){
					if(lock.getHoldCount()==1){
						isClosed = true;
						locks.remove(identifier);
					}
				}
			} finally{
				lock.unlock();
			}
		}

		private boolean lock(){
			try {
				if(!lock.tryLock(0, TimeUnit.MILLISECONDS)){
					do {
						checkForDeadlock();
					} while (!lock.tryLock(100, TimeUnit.MILLISECONDS));
				}
			} catch (InterruptedException e) {
				throw new LockAcquisitionFailedException("Thread was interrupted", e);
			}
			if(isClosed){
				lock.unlock();
				return false;
			}
			return true;
		}

		private void checkForDeadlock() {
			if(!lock.isHeldByCurrentThread()&&lock.isLocked()){
				for(Thread thread : threadsWaitingForMyLocks(Thread.currentThread())){
					if(lock.isHeldBy(thread)){
						throw new DeadlockException("An imminent deadlock was detected while attempting to acquire a lock");
					}
				}
			}
		}
		
		public Collection<Thread> queuedThreads() {
            return lock.getQueuedThreads();
        }

        public boolean isHeldBy(Thread owner) {
            return lock.isHeldBy(owner);
        }
		
	}
	
	
	private static final class PubliclyOwnedReentrantLock extends ReentrantLock {
		
		private static final long serialVersionUID = -2259228494514612163L;

        @Override
        public Collection<Thread> getQueuedThreads() { // NOSONAR
            return super.getQueuedThreads();
        }

        public boolean isHeldBy(Thread thread) {
            return thread.equals(getOwner());
        }
	}
	
}
