package com.sundy.axon.cache;

import java.io.Serializable;

import javax.cache.Cache;
import javax.cache.configuration.CacheEntryListenerConfiguration;
import javax.cache.configuration.Factory;
import javax.cache.event.CacheEntryCreatedListener;
import javax.cache.event.CacheEntryEvent;
import javax.cache.event.CacheEntryEventFilter;
import javax.cache.event.CacheEntryExpiredListener;
import javax.cache.event.CacheEntryListener;
import javax.cache.event.CacheEntryListenerException;
import javax.cache.event.CacheEntryRemovedListener;
import javax.cache.event.CacheEntryUpdatedListener;

public class JCacheAdapter extends AbstractCacheAdapter<CacheEntryListenerConfiguration> {

	private final javax.cache.Cache jCache;
	
	public JCacheAdapter(Cache jCache) {
		this.jCache = jCache;
	}

	public <K, V> V get(K key) {
		return (V) jCache.get(key);
	}

	public <K, V> void put(K key, V value) {
		jCache.put(key, value);
	}

	public <K, V> boolean putIfAbsent(K key, V value) {
		return jCache.putIfAbsent(key, value);
	}

	public <K> boolean remove(K key) {
		return jCache.remove(key);
	}

	public <K> boolean containsKey(K key) {
		return jCache.containsKey(key);
	}

	@Override
	protected CacheEntryListenerConfiguration createListenerAdapter(
			EntryListener cacheEntryListener) {
		return new JCacheListenerAdapter(cacheEntryListener);
	}

	@Override
	public void doRegisterListener(CacheEntryListenerConfiguration adapter) {
		jCache.registerCacheEntryListener(adapter);
	}

	@Override
	public void doUnRegisterListener(CacheEntryListenerConfiguration adapter) {
		jCache.deregisterCacheEntryListener(adapter);
	}
	
	private static final class JCacheListenerAdapter<K,V> implements CacheEntryListenerConfiguration<K, V>,
		CacheEntryUpdatedListener<K, V>,CacheEntryCreatedListener<K, V>,CacheEntryExpiredListener<K, V>,
		CacheEntryRemovedListener<K, V>,Factory<CacheEntryListener<? super K, ? super V>>,Serializable{

		private static final long serialVersionUID = 3260575514029378445L;
		
		private final EntryListener entryListener;
		
		public JCacheListenerAdapter(EntryListener entryListener) {
			this.entryListener = entryListener;
		}

		public CacheEntryListener<? super K, ? super V> create() {
			return this;
		}

		public void onRemoved(
				Iterable<CacheEntryEvent<? extends K, ? extends V>> iterable)
				throws CacheEntryListenerException {
			for(CacheEntryEvent event : iterable){
				entryListener.onEntryRemoved(event);
			}
		}

		public void onExpired(
				Iterable<CacheEntryEvent<? extends K, ? extends V>> iterable)
				throws CacheEntryListenerException {
			for(CacheEntryEvent event : iterable){
				entryListener.onEntryExpired(event);
			}
		}

		public void onCreated(
				Iterable<CacheEntryEvent<? extends K, ? extends V>> iterable)
				throws CacheEntryListenerException {
			for(CacheEntryEvent event : iterable){
				entryListener.onEntryCreated(event.getKey(), event.getValue());
			}
		}

		public void onUpdated(
				Iterable<CacheEntryEvent<? extends K, ? extends V>> iterable)
				throws CacheEntryListenerException {
			for(CacheEntryEvent event : iterable){
				entryListener.onEntryUpdate(event.getKey(), event.getValue());
			}
		}

		public Factory<CacheEntryEventFilter<? super K, ? super V>> getCacheEntryEventFilterFactory() {
			return null;
		}

		public Factory<CacheEntryListener<? super K, ? super V>> getCacheEntryListenerFactory() {
			return this;
		}

		public boolean isOldValueRequired() {
			return false;
		}

		public boolean isSynchronous() {
			return true;
		}
		
	}

}
