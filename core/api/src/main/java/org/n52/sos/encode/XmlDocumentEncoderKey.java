package org.n52.sos.encode;

import org.n52.sos.util.ClassHelper;

import com.google.common.base.Objects;

public class XmlDocumentEncoderKey extends XmlEncoderKey {

    public XmlDocumentEncoderKey(String namespace, Class<?> type) {
        super(namespace, type);
    }

    @Override
    public String toString() {
        return String.format("XmlDocumentEncoderKey[namespace=%s, type=%s]", getNamespace(), getType().getSimpleName());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && getClass() == obj.getClass()) {
            final XmlDocumentEncoderKey o = (XmlDocumentEncoderKey) obj;
            return Objects.equal(getType(), o.getType()) && Objects.equal(getNamespace(), o.getNamespace());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(3, 79, getNamespace(), getType());
    }

    @Override
    public int getSimilarity(EncoderKey key) {
        if (key instanceof XmlDocumentEncoderKey) {
            XmlDocumentEncoderKey xmlKey = (XmlDocumentEncoderKey) key;
            if (Objects.equal(getNamespace(), xmlKey.getNamespace())) {
                return ClassHelper.getSimiliarity(getType() != null ? getType() : Object.class,
                        xmlKey.getType() != null ? xmlKey.getType() : Object.class);
            } else {
                return -1;
            }
        } else {
            return -1;
        }
    }
}
