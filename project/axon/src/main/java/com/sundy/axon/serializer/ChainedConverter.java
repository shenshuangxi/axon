package com.sundy.axon.serializer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.sundy.axon.common.Assert;

/**
 * 代理转换器，该转换器是一条链，该链子上包含一系列的转换器用于转换数据类型，单个的类型转换器不必要采用这个
 * @author Administrator
 *
 * @param <S> 转换器需要的原数据
 * @param <T> 转换器输出的目标数据
 */
public class ChainedConverter<S, T> implements ContentTypeConverter<S, T> {

	private final List<ContentTypeConverter> delegates;
    private final Class<T> target;
    private final Class<S> source;
	
    /**
     * 返回一个转换器，可以使用由给定候选项形成的链，将IntermediateRepresentation从给定的sourceType转换为给定的targetType。 返回的转换器使用一些（或全部）给定候选者作为代表。
     * @param sourceType
     * @param targetType
     * @param candidates
     * @return
     */
    public static <S, T> ChainedConverter<S, T> calculateChain(Class<S> sourceType, Class<T> targetType,
            Collection<ContentTypeConverter<?, ?>> candidates) {
		Route route = calculateRoute(sourceType, targetType, candidates);
		if (route == null) {
		throw new CannotConvertBetweenTypesException(String.format("Cannot build a converter to convert from %s to %s",
		             sourceType.getName(), targetType.getName()));
		}
		return new ChainedConverter<S, T>(route.asList());
	}
    
    /**
     * 指示此转换器是否能够使用给定的转换器将给定的sourceContentType转换为targetContentType。 如果是真的，它可以使用任意数量的给定转换器来形成链。
     * @param sourceContentType
     * @param targetContentType
     * @param converters
     * @return
     */
    public static <S, T> boolean canConvert(Class<S> sourceContentType, Class<T> targetContentType,
            List<ContentTypeConverter<?, ?>> converters) {
    	return calculateRoute(sourceContentType, targetContentType, converters) != null;
	}
    
    private static <S, T> Route calculateRoute(Class<S> sourceType, Class<T> targetType,
            Collection<ContentTypeConverter<?, ?>> candidates) {
    	return new RouteCalculator(candidates).calculateRoute(sourceType, targetType);
	}
    
    /**
     * 创建一个新的实例，使用给定的代表来形成一个转换器链。 请注意，代表必须为Continuous链，这意味着每个项必须产生可由下一个委托使用的类型的IntermediateRepresentation。
     * <p/>
     * 要自动计算转换器之间的路由，请参阅calculateChain（源，目标，候选）
     * @param delegates
     */
    public ChainedConverter(List<ContentTypeConverter> delegates) {
        Assert.isTrue(delegates != null && !delegates.isEmpty(), "The given delegates may not be null or empty");
        Assert.isTrue(isContinuous(delegates), "The given delegates must form a continuous chain");
        this.delegates = new ArrayList<ContentTypeConverter>(delegates);
        target = this.delegates.get(this.delegates.size() - 1).targetType();
        source = delegates.get(0).expectedSourceType();
    }
    
    private boolean isContinuous(List<ContentTypeConverter> candidates) {
        Class current = null;
        for (ContentTypeConverter candidate : candidates) {
            if (current == null || current.equals(candidate.expectedSourceType())) {
                current = candidate.targetType();
            } else {
                return false;
            }
        }
        return true;
    }
		
	public Class<S> expectedSourceType() {
		return source;
	}

	public Class<T> targetType() {
		return target;
	}

	public SerializedObject<T> convert(SerializedObject<S> original) {
		SerializedObject intermediate = original;
        for (ContentTypeConverter step : delegates) {
            intermediate = step.convert(intermediate);
        }
        return intermediate;
	}

	public T convert(S original) {
		Object intermediate = original;
        for (ContentTypeConverter step : delegates) {
            intermediate = step.convert(intermediate);
        }
        return (T) intermediate;
	}
	
	 private static final class RouteCalculator {

	        private final Collection<ContentTypeConverter<?, ?>> candidates;
	        private final List<Route> routes = new LinkedList<Route>();

	        private RouteCalculator(Collection<ContentTypeConverter<?, ?>> candidates) {
	            this.candidates = new CopyOnWriteArrayList<ContentTypeConverter<?, ?>>(candidates);
	        }

	        private Route calculateRoute(Class<?> sourceType, Class<?> targetType) {
	            Route match = buildInitialRoutes(sourceType, targetType);
	            if (match != null) {
	                return match;
	            }
	            while (!candidates.isEmpty() && !routes.isEmpty()) {
	                Route route = getShortestRoute();
	                for (ContentTypeConverter candidate : candidates) {
	                    if (route.endPoint().equals(candidate.expectedSourceType())) {
	                        Route newRoute = route.joinedWith(candidate);
	                        candidates.remove(candidate);
	                        if (targetType.equals(newRoute.endPoint())) {
	                            return newRoute;
	                        }
	                        routes.add(newRoute);
	                    }
	                }
	                routes.remove(route);
	            }
	            return null;
	        }

	        private Route buildInitialRoutes(Class<?> sourceType, Class<?> targetType) {
	            for (ContentTypeConverter converter : candidates) {
	                if (sourceType.equals(converter.expectedSourceType())) {
	                    Route route = new Route(converter);
	                    if (route.endPoint().equals(targetType)) {
	                        return route;
	                    }
	                    routes.add(route);
	                    candidates.remove(converter);
	                }
	            }
	            return null;
	        }

	        private Route getShortestRoute() {
	            // since all nodes have equal distance, the first (i.e. oldest) node is the shortest
	            return routes.get(0);
	        }
	    }
	
	private static final class Route {

        private final ContentTypeConverter[] nodes;
        private final Class endPoint;

        private Route(ContentTypeConverter initialVertex) {
            this.nodes = new ContentTypeConverter[]{initialVertex};
            endPoint = initialVertex.targetType();
        }

        private Route(ContentTypeConverter[] baseNodes, ContentTypeConverter newDestination) {
            nodes = Arrays.copyOf(baseNodes, baseNodes.length + 1);
            nodes[baseNodes.length] = newDestination;
            endPoint = newDestination.targetType();
        }

        private Route joinedWith(ContentTypeConverter newVertex) {
            Assert.isTrue(endPoint.equals(newVertex.expectedSourceType()),
                          "Cannot append a vertex if it does not start where the current Route ends");
            return new Route(nodes, newVertex);
        }

        private Class<?> endPoint() {
            return endPoint;
        }

        private List<ContentTypeConverter> asList() {
            return Arrays.asList(nodes);
        }
    }

}
