package org.n52.sos.ds.procedure.generator;

import java.util.Collection;
import java.util.Map;

import org.n52.janmayen.Producer;
import org.n52.janmayen.component.AbstractComponentRepository;
import org.n52.janmayen.component.Component;
import org.n52.janmayen.component.ComponentFactory;
import org.n52.janmayen.lifecycle.Constructable;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Maps;

public abstract class AbstractProcedureDescriptionGeneratorFactoryRepository<K, C extends Component<K>, F extends ComponentFactory<K, C>>
        extends
        AbstractComponentRepository<K, C, F>
        implements
        Constructable {

    private final Map<K, Producer<C>> factories = Maps.newHashMap();

    @Autowired(required = false)
    private Collection<C> components;

    @Autowired(required = false)
    private Collection<F> componentFactories;

    @Override
    public void init() {
        Map<K, Producer<C>> implementations = getUniqueProviders(this.components, this.componentFactories);
        this.factories.clear();
        this.factories.putAll(implementations);
    }

    public abstract C getFactory(String descriptionFormat);

    public abstract C getFactory(K key);

    /**
     * Checks if a factory is available to generate the description
     *
     * @param descriptionFormat
     *            Default format
     *
     * @return If a factory is available
     */
    public abstract boolean hasProcedureDescriptionGeneratorFactory(String descriptionFormat);


   protected Map<K, Producer<C>> getFactories() {
       return factories;
   }
}
