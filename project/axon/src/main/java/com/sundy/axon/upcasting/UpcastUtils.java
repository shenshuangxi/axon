/*
 * Copyright (c) 2010-2014. Axon Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sundy.axon.upcasting;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sundy.axon.domain.DomainEventMessage;
import com.sundy.axon.serializer.SerializedDomainEventData;
import com.sundy.axon.serializer.SerializedDomainEventMessage;
import com.sundy.axon.serializer.SerializedObject;
import com.sundy.axon.serializer.Serializer;
import com.sundy.axon.serializer.UnknownSerializedTypeException;

import java.util.ArrayList;
import java.util.List;

/**
 * 优化与类型提升有关的任务的实用类。
 */
public abstract class UpcastUtils {

    private static final Logger logger = LoggerFactory.getLogger(UpcastUtils.class);

    private UpcastUtils() {
    }

    
    public static List<DomainEventMessage> upcastAndDeserialize(SerializedDomainEventData entry,
                                                                Object aggregateIdentifier,
                                                                Serializer serializer, UpcasterChain upcasterChain,
                                                                boolean skipUnknownTypes) {
        SerializedDomainEventUpcastingContext context = new SerializedDomainEventUpcastingContext(entry, serializer);
        List<SerializedObject> objects = upcasterChain.upcast(entry.getPayload(), context);
        List<DomainEventMessage> events = new ArrayList<DomainEventMessage>(objects.size());
        for (SerializedObject object : objects) {
            try {
                DomainEventMessage<Object> message = new SerializedDomainEventMessage<Object>(
                        new UpcastSerializedDomainEventData(entry,
                                                            firstNonNull(aggregateIdentifier,
                                                                         entry.getAggregateIdentifier()), object),
                        serializer);

                // prevents duplicate deserialization of meta data when it has already been access during upcasting
                if (context.getSerializedMetaData().isDeserialized()) {
                    message = message.withMetaData(context.getSerializedMetaData().getObject());
                }
                events.add(message);
            } catch (UnknownSerializedTypeException e) {
                if (!skipUnknownTypes) {
                    throw e;
                }
                logger.info("Ignoring event of unknown type {} (rev. {}), as it cannot be resolved to a Class",
                            object.getType().getName(), object.getType().getRevision());
            }
        }
        return events;
    }

    private static Object firstNonNull(Object... instances) {
        for (Object instance : instances) {
            if (instance != null) {
                return instance;
            }
        }
        return null;
    }
}
