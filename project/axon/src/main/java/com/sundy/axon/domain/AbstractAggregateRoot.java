package com.sundy.axon.domain;

import java.io.Serializable;

import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

@MappedSuperclass
public abstract class AbstractAggregateRoot<I> implements AggregateRoot<I>,Serializable {

	@Transient
	private volatile EventContainer eventContainer;

}
