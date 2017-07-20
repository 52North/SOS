package org.n52.sos.ds.hibernate.util;

import static org.n52.sos.ds.hibernate.util.HibernateCollectors.toConjunction;
import static org.n52.sos.ds.hibernate.util.HibernateCollectors.toDisjunction;

import java.util.Arrays;
import java.util.Optional;

import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Junction;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 */
public class MoreRestrictions {

    private MoreRestrictions() {
    }

    @SafeVarargs
    @SuppressWarnings(value = "varargs")
    public static Optional<? extends Criterion> and(Optional<? extends Criterion>... criteria) {
        Conjunction conjunction = Arrays.stream(criteria).filter(Optional::isPresent).map(Optional::get)
                .collect(toConjunction());
        return Optional.of(conjunction).filter(MoreRestrictions::hasConditions);
    }

    @SafeVarargs
    @SuppressWarnings(value = "varargs")
    public static Optional<? extends Criterion> or(Optional<? extends Criterion>... criteria) {
        Disjunction disjunction = Arrays.stream(criteria).filter(Optional::isPresent).map(Optional::get)
                .collect(toDisjunction());
        return Optional.of(disjunction).filter(MoreRestrictions::hasConditions);
    }

    public static boolean hasConditions(Junction j) {
        return j.conditions().iterator().hasNext();
    }

}
