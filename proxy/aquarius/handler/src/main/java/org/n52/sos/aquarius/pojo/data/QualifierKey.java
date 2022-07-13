package org.n52.sos.aquarius.pojo.data;

import java.io.Serializable;
import java.util.Objects;


public class QualifierKey implements Serializable {

    private static final long serialVersionUID = 1671220620564807413L;
    public static final String BELOW = "below";
    public static final String ABOVE = "above";
    private final String value;

    public QualifierKey(String value) {
        this.value = value;
    }

    public static QualifierKey of(String value) {
        return new QualifierKey(value);
    }

    public String getValue() {
        return value;
    }

    public boolean isEquals(Object obj) {
        if (!(obj instanceof QualifierKey || obj instanceof String)) {
            return false;
        }
        if (obj instanceof String) {
            String that = (String) obj;
            return Objects.equals(this.getValue(), that);
        }
        QualifierKey that = (QualifierKey) obj;
        return Objects.equals(this.getValue(), that.getValue());
    }
}
