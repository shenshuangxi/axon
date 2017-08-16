package com.sundy.axon.saga.annotation;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import com.sundy.axon.saga.AssociationValue;
import com.sundy.axon.saga.AssociationValues;

/**
 * 默认实现的AssociationValues接口。 这个实现是完全可序列化的
 * @author Administrator
 *
 */
public class AssociationValuesImpl implements AssociationValues, Serializable {

	private static final long serialVersionUID = 8273718165811296962L;
	
	private final Set<AssociationValue> values = new CopyOnWriteArraySet<AssociationValue>();
    private transient Set<AssociationValue> addedValues = new HashSet<AssociationValue>();
    private transient Set<AssociationValue> removedValues = new HashSet<AssociationValue>();
    
	public Iterator<AssociationValue> iterator() {
		return Collections.unmodifiableSet(values).iterator();
	}
	public Set<AssociationValue> removedAssociations() {
		if (removedValues == null || removedValues.isEmpty()) {
            return Collections.emptySet();
        }
        return removedValues;
	}
	public Set<AssociationValue> addedAssociations() {
		if (addedValues == null || addedValues.isEmpty()) {
            return Collections.emptySet();
        }
        return addedValues;
	}
	public void commit() {
		if (addedValues != null) {
            addedValues.clear();
        }
        if (removedValues != null) {
            removedValues.clear();
        }
	}
	public int size() {
		return values.size();
	}
	public boolean contains(AssociationValue associationValue) {
		return values.contains(associationValue);
	}
	public boolean add(AssociationValue associationValue) {
		final boolean added = values.add(associationValue);
        if (added) {
                initializeChangeTrackers();
                if (!removedValues.remove(associationValue)) {
                    addedValues.add(associationValue);
                }
        }
        return added;
	}
	public boolean remove(AssociationValue associationValue) {
		final boolean removed = values.remove(associationValue);
        if (removed) {
                initializeChangeTrackers();
                if (!addedValues.remove(associationValue)) {
                	removedValues.add(associationValue);
                }
        }
        return removed;
	}
	
	private void initializeChangeTrackers(){
		if (removedValues == null) {
            removedValues = new HashSet<AssociationValue>();
        }
        if (addedValues == null) {
            addedValues = new HashSet<AssociationValue>();
        }
	}
	
	public Set<AssociationValue> asSet() {
		return Collections.unmodifiableSet(values);
	}
	

}
