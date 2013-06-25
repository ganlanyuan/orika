package ma.glasnost.orika.impl;

import java.util.List;

import ma.glasnost.orika.Filter;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Property;
import ma.glasnost.orika.metadata.Type;
import ma.glasnost.orika.metadata.TypeFactory;

/**
 * AggregateFilter provides the capability to combine multiple
 * filters together to act as a single filter
 * 
 * @author mattdeboer
 *
 */
public class AggregateFilter implements Filter<Object, Object>{
    
    private final List<Filter<Object, Object>> filters;
    private final boolean filtersSource;
    private final boolean filtersDestination;
    
    /**
     * Constructs a new Aggregate Filter composed of the provided filters
     * 
     * @param filters
     */
    public AggregateFilter(List<Filter<Object, Object>> filters) {
        this.filters = filters;
        boolean sourceIsFiltered = false;
        boolean destIsFiltered = false;
        for (Filter<Object, Object> filter: filters) {
            if (filter.filtersSource()) {
                sourceIsFiltered = true;
            }
            if (filter.filtersDestination()) {
                destIsFiltered = true;
            }
        }
        this.filtersSource = sourceIsFiltered;
        this.filtersDestination = destIsFiltered;
        
    }

    public Type<Object> getAType() {
        return TypeFactory.TYPE_OF_OBJECT;
    }

    public Type<Object> getBType() {
        return TypeFactory.TYPE_OF_OBJECT;
    }

    public boolean appliesTo(Property source, Property destination) {
        return true;
    }

    public boolean filtersSource() {
        return filtersSource;
    }

    public boolean filtersDestination() {
        return filtersDestination;
    }

    public boolean shouldMap(Type<?> sourceType, String sourceName, Type<?> destType, String destName, MappingContext mappingContext) {
        boolean shouldMap = true;
        for (Filter<Object, Object> filter: filters) {
            if (!filter.shouldMap(sourceType, sourceName, destType, destName, mappingContext)) {
                shouldMap = false;
                break;
            }
        }
        return shouldMap;
    }

    public <D> D filterDestination(D destinationValue, Type<?> sourceType, String sourceName, Type<D> destType, String destName, MappingContext mappingContext) {
        D value = destinationValue;
        for (Filter<Object, Object> filter: filters) {
            if (filter.filtersDestination()) {
                value = filter.filterDestination(value, sourceType, sourceName, destType, destName, mappingContext);
            }
        }
        return value;
    }

    public <S> S filterSource(S sourceValue, Type<S> sourceType, String sourceName, Type<?> destType, String destName, MappingContext mappingContext) {
        S value = sourceValue;
        for (Filter<Object, Object> filter: filters) {
            if (filter.filtersDestination()) {
                value = filter.filterSource(value, sourceType, sourceName, destType, destName, mappingContext);
            }
        }
        return value;
    }
    
    
}
