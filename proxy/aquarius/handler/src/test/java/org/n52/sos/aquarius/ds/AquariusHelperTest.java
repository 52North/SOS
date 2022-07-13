package org.n52.sos.aquarius.ds;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AquariusHelperTest {
    
    @Test
    public void initial_grades_loaeding() {
        AquariusHelper aquariusHelper = new AquariusHelper();
        aquariusHelper.init();
        Assertions.assertTrue(aquariusHelper.getGrades().size() > 0);
    }

}
