package org.n52.sos.util;


import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.joda.time.DateTime;
import org.junit.Test;

public class JavaHelperTest {
    
    @Test
    public void test() {
        JavaHelper.generateID("123" + System.currentTimeMillis());
    }
    
    @Test
    public void test_generateID() { 
        String generateID = JavaHelper.generateID("123" + System.currentTimeMillis());
        String generateID2 = JavaHelper.generateID("123" + System.currentTimeMillis());
        assertThat(generateID.equals(generateID2), is(false));
    }
    
    @Test
    public void test_generateID_2() {
        String id = "123" + System.currentTimeMillis();
        String generateID = JavaHelper.generateID(id);
        String generateID2 = JavaHelper.generateID(id);
        assertThat(generateID.equals(generateID2), is(false));
    }
    
}
