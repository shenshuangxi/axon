package com.sundy.axon.cache;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;

public class WeakReferenceCache implements Cache {

	private final ConcurrentMap<Object, Entry> cache = new ConcurrentHashMap<Object, WeakReferenceCache.Entry>();
	private final ReferenceQueue<Object> referenceQueue = new ReferenceQueue<Object>();
	private final Set<EntryListener> adapters = new CopyOnWriteArraySet<Cache.EntryListener>();
	
	public <K, V> V get(K key) {
		purgeItems();
		final Reference<Object> entry = cache.get(key);
		final V retrunValue = (V) (entry==null ? null : entry.get());
		if(retrunValue != null){
			for(EntryListener adapter : adapters){
				adapter.onEntryRead(key, retrunValue);
			}
		}
		return retrunValue;
	}

	public <K, V> void put(K key, V value) {
		if(value == null){
			throw new IllegalArgumentException("Null values not supported");
		}
		purgeItems();
		if(cache.put(key, new Entry(key, value))!=null){
			for(EntryListener adapter : adapters){
				adapter.onEntryUpdate(key, value);
			}
		}else{
			for(EntryListener adapter : adapters){
				adapter.onEntryCreated(key, value);
			}
		}

	}

	public <K, V> boolean putIfAbsent(K key, V value) {
		if(value == null){
			throw new IllegalArgumentException("Null values not supported");
		}
		purgeItems();
		if(cache.putIfAbsent(key, new Entry(key, value))==null){
			for(EntryListener adapter : adapters){
				adapter.onEntryCreated(key, value);
			}
			return true;
		}
		return false;
	}

	public <K> boolean remove(K key) {
		if(cache.remove(key)!=null){
			for(EntryListener adapter : adapters){
				adapter.onEntryRemoved(key);
			}
			return true;
		}
		return false;
	}

	public <K> boolean containsKey(K key) {
		purgeItems();
		final Reference<Object> entry = cache.get(key);
		return entry!=null && entry.get()!=null;
	}

	public void registerCacheEntryListener(EntryListener cacheEntryListener) {
		this.adapters.add(cacheEntryListener);
	}

	public void unregisterCacheEntryListener(EntryListener cacheEntryListener) {
		this.adapters.remove(cacheEntryListener);
	}
	
	private void purgeItems(){
		Entry purgedEntry;
		while((purgedEntry=(Entry) referenceQueue.poll())!=null){
			if(cache.remove(purgedEntry.getKey())!=null){
				for(EntryListener entryListener : adapters){
					entryListener.onEntryExpired(purgedEntry.getKey());
				}
			}
		}
	}
	
	private class Entry extends WeakReference<Object> {
		private final Object key;
		
		public Entry(Object key, Object value){
			super(key, referenceQueue);
			this.key = key;
		}
		
		public Object getKey(){
			return key;
		}
	}

}
