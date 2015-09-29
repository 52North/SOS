package org.n52.sos.encode;

import org.n52.sos.util.ClassHelper;

import com.google.common.base.Objects;

public class ClassToClassEncoderKey implements EncoderKey {
    
    private final  Class<?> internalClass;

    private final Class<?> encodedClass;

    public ClassToClassEncoderKey(Class<?> internalClass, Class<?> encodedClass) {
        this.internalClass = internalClass;
        this.encodedClass = encodedClass;
    }
    
    public ClassToClassEncoderKey(Object internalClass, Object encodedClass) {
        this(internalClass.getClass(), encodedClass.getClass());
    }

    public Class<?> getInternalClass() {
        return internalClass;
    }

    public Class<?> getEncodedClass() {
        return encodedClass;
    }

    @Override
    public String toString() {
        return String.format("ClassToClassEncoderKey[internalClass=%s, encodedClass=%s]", getInternalClass().getSimpleName(), getEncodedClass().getSimpleName());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && getClass() == obj.getClass()) {
            final ClassToClassEncoderKey o = (ClassToClassEncoderKey) obj;
            return Objects.equal(getEncodedClass(), o.getEncodedClass()) && Objects.equal(getInternalClass(), o.getInternalClass());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(3, 79, getInternalClass(), getEncodedClass());
    }

    @Override
    public int getSimilarity(EncoderKey key) {
        if (key instanceof ClassToClassEncoderKey) {
            ClassToClassEncoderKey xmlKey = (ClassToClassEncoderKey) key;
            if (Objects.equal(getInternalClass(), xmlKey.getInternalClass())) {
                return ClassHelper.getSimiliarity(getEncodedClass() != null ? getEncodedClass() : Object.class,
                        xmlKey.getEncodedClass() != null ? xmlKey.getEncodedClass() : Object.class);
            } else {
                return -1;
            }
        } else {
            return -1;
        }
    }

}
