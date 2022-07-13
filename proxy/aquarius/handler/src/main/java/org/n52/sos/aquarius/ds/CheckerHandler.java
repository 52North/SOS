package org.n52.sos.aquarius.ds;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.n52.sos.aquarius.pojo.data.Point;

public class CheckerHandler {
    
    private Set<Checker> checkers = new LinkedHashSet<>();
    
    public CheckerHandler addChecker(Checker checker) {
        if (checker != null) {
            this.checkers.add(checker);
        }
        return this;
    }
    
    public List<Point> check(List<Point> points) {
        return points.stream()
                .map(p -> check(p))
                .collect(Collectors.toList());
    }

    public Point check(Point p) {
        for (Checker checker : checkers) {
            checker.check(p);
        }
        return p;
    }
    
    

}
