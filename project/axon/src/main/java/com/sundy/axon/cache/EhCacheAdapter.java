package com.sundy.axon.cache;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.event.CacheEventListener;

public class EhCacheAdapter extends AbstractCacheAdapter<CacheEventListener> {

	private final Ehcache ehcache;
	
	public EhCacheAdapter(Ehcache ehcache){
		this.ehcache = ehcache;
	}
	
	@SuppressWarnings("unchecked")
	public <K, V> V get(K key) {
		final Element element = ehcache.get(key);
		return element == null ? null : (V) element.getObjectValue();
	}

	public <K, V> void put(K key, V value) {
		ehcache.put(new Element(key, value));
	}

	public <K, V> boolean putIfAbsent(K key, V value) {
		return ehcache.putIfAbsent(new Element(key, value)) == null;
	}
	
	public <K> boolean remove(K key) {
		return ehcache.remove(key);
	}

	public <K> boolean containsKey(K key) {
		return ehcache.isKeyInCache(key);
	}

	@Override
	protected CacheEventListener createListenerAdapter(
			EntryListener entryListener) {
		return new CacheEventListenerAdapter(ehcache, entryListener);
	}

	@Override
	public void doRegisterListener(CacheEventListener cacheEventListener) {
		ehcache.getCacheEventNotificationService().registerListener(cacheEventListener);
	}

	@Override
	public void doUnRegisterListener(CacheEventListener cacheEventListener) {
		ehcache.getCacheEventNotificationService().unregisterListener(cacheEventListener);
	}
	
	private static class CacheEventListenerAdapter implements CacheEventListener, Cloneable{

		private Ehcache ehcache;
		private EntryListener entryListener;
		
		public CacheEventListenerAdapter(Ehcache ehcache,
				EntryListener entryListener) {
			this.ehcache = ehcache;
			this.entryListener = entryListener;
		}

		public void dispose() {
			
		}

		public void notifyElementEvicted(Ehcache ehcache, Element element) {
			if (ehcache.equals(ehcache)){
				entryListener.onEntryExpired(element.getObjectKey());
			}
		}

		public void notifyElementExpired(Ehcache ehcache, Element element) {
			if (ehcache.equals(ehcache)){
				entryListener.onEntryExpired(element.getObjectKey());
			}
		}

		public void notifyElementPut(Ehcache ehcache, Element element)
				throws CacheException {
			if (ehcache.equals(ehcache)){
				entryListener.onEntryUpdate(element.getObjectKey(), element.getObjectValue());
			}
		}

		public void notifyElementRemoved(Ehcache ehcache, Element element)
				throws CacheException {
			if (ehcache.equals(ehcache)){
				entryListener.onEntryRemoved(element.getObjectKey());
			}
		}

		public void notifyElementUpdated(Ehcache ehcache, Element element)
				throws CacheException {
			if (ehcache.equals(ehcache)){
				entryListener.onEntryUpdate(element.getObjectKey(), element.getObjectValue());
			}
		}

		public void notifyRemoveAll(Ehcache ehcache) {
			// TODO Auto-generated method stub
			
		}

		public Object clone() throws CloneNotSupportedException {
			CacheEventListenerAdapter clone = (CacheEventListenerAdapter) super.clone();
            clone.ehcache = (Ehcache) ehcache.clone();
            clone.entryListener = (EntryListener) entryListener.clone();
            return clone;
		}
		
		
	}

}
