package com.sundy.axon.cache;

/**
 * 抽象的缓存机制，所有的axon组件都依赖这个机制
 * @author Administrator
 *
 */
public interface Cache {

	<K, V> V get(K key);
	
	<K, V> void put(K key, V value);
	
	<K, V> boolean putIfAbsent(K key, V value);
	
	<K> boolean remove(K key);
	
	<K> boolean containsKey(K key);
	
	void registerCacheEntryListener(EntryListener cacheEntryListener);
	
	void unregisterCacheEntryListener(EntryListener cacheEntryListener);
	
	interface EntryListener {
		
		void onEntryExpired(Object key);
		
		void onEntryRemoved(Object key);
		
		void onEntryUpdate(Object key, Object value);
		
		void onEntryCreated(Object key, Object value);
		
		void onEntryRead(Object key, Object value);
		
		Object clone() throws CloneNotSupportedException;
	}
	
	/**
	 * EntryListener的适配器，允许重写所有的具体的回调函数
	 * @author Administrator
	 */
	class EntryListenerAdapter implements EntryListener {

		public void onEntryExpired(Object key) {
			// TODO Auto-generated method stub
			
		}

		public void onEntryRemoved(Object key) {
			// TODO Auto-generated method stub
			
		}

		public void onEntryUpdate(Object key, Object value) {
			// TODO Auto-generated method stub
			
		}

		public void onEntryCreated(Object key, Object value) {
			// TODO Auto-generated method stub
			
		}

		public void onEntryRead(Object key, Object value) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public Object clone() throws CloneNotSupportedException {
			return super.clone();
		}
		
	}
	
}
