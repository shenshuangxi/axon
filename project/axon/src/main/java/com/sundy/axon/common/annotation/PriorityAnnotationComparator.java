package com.sundy.axon.common.annotation;

import java.util.Comparator;

import com.sundy.axon.common.Priority;

public class PriorityAnnotationComparator<T> implements Comparator<T> {

	private static final PriorityAnnotationComparator INSTANCE = new PriorityAnnotationComparator();
	
	public static <T> PriorityAnnotationComparator<T> getInstance() {
        return INSTANCE;
    }
	
	private PriorityAnnotationComparator() {
    }

    public int compare(T o1, T o2) {
        Priority annotation1 = o1.getClass().getAnnotation(Priority.class);
        Priority annotation2 = o2.getClass().getAnnotation(Priority.class);
        int prio1 = annotation1 == null ? Priority.NEUTRAL : annotation1.value();
        int prio2 = annotation2 == null ? Priority.NEUTRAL : annotation2.value();

        return (prio1 > prio2) ? -1 : ((prio2 == prio1) ? 0 : 1);
    }

}
