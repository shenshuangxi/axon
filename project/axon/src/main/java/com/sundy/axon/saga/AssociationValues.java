package com.sundy.axon.saga;

import java.util.Set;

/**
 * 接口用于描述一个容器，该容器用于容纳一个{@link Saga}实例所有的{@link AssociationValue Association Values} 
 * 此容器跟踪提交之前对其内容所做的更改 (see {@link #commit()})
 * @author Administrator
 *
 */
public interface AssociationValues extends Iterable<AssociationValue> {

	/**
	 * 返回最后一次提交{@link AssociationValues#commit()} 所移除掉的关联值
	 * <p/>
	 * 如果关联值在提交之前添加或移除，该方法时不会返回的
	 * @return
	 */
	Set<AssociationValue> removedAssociations();
	
	/**
	 * 返回最后一次提交{@link AssociationValues#commit()} 所增加的关联值
	 * <p/>
	 * 如果关联值在提交之前添加或移除，该方法时不会返回的
	 * @return
	 */
	Set<AssociationValue> addedAssociations();
	
	/**
	 * 重置跟踪改变,清空所有数据
	 */
	void commit();
	
	/**
	 * 返回当前容器内所有关联值的个数
	 * @return
	 */
	int size();
	
	/**
	 * 判断容器是否哈有传入的关联值
	 * @param associationValue
	 * @return
	 */
	boolean contains(AssociationValue associationValue);
	
	/**
	 * 如果容器内没有传入的关联值，向容器添加关联值。
	 * 如果添加成功，可以通过{@link #addedAssociations()} 返回。除非在后面的调用{@link #removedAssociations()}中被移除掉了
	 * @param associationValue
	 * @return 添加成功返回true 如果之前已存在返回false
	 */
	boolean add(AssociationValue associationValue);
	
	/**
	 * 如果容器内没有传入的关联值，向容器移除关联值。
	 * 如果移除成功，可以通过{@link #removedAssociations()} 返回。除非在后面的调用{@link #addedAssociations()}中被移除掉了
	 * @param associationValue
	 * @return 移除成功返回true 如果之前已不存在返回false
	 */
	boolean remove(AssociationValue associationValue);
	
	/**
	 * 将此实例作为一组关联值返回。 返回的集合是此容器上的只读视图。 在调用后对容器进行的任何更改都可能反映在返回的集合中。
	 * @return
	 */
	Set<AssociationValue> asSet();
	
}
