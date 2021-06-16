package org.n52.sos.aquarius.ds;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.n52.sos.aquarius.pojo.data.Point;
import org.n52.sos.aquarius.pojo.data.Qualifier;

public class QualifierChecker implements Serializable {

    private static final long serialVersionUID = 1L;

    private Set<Qualifier> qualifiers = new LinkedHashSet<>();

    public QualifierChecker addQualifier(Qualifier qualifier) {
        if (qualifier != null) {
            this.qualifiers.add(qualifier);
        }
        return this;
    }

    public Point check(Point point) {
        if (point != null) {
            for (Qualifier qualifier : qualifiers) {
                qualifier.applyQualifier(point);
            }
        }
        return point;
    }

    public List<Point> check(List<Point> points) {
        return points.stream()
                .map(p -> check(p))
                .collect(Collectors.toList());
    }

}
