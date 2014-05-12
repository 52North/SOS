package org.n52.sos.ogc.swe.stream;

import org.n52.sos.ogc.om.StreamingValue;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.swe.SweAbstractDataComponent;
import org.n52.sos.ogc.swe.SweConstants.SweDataComponentType;
import org.n52.sos.ogc.swe.encoding.SweAbstractEncoding;
import org.n52.sos.ogc.swe.simpleType.SweCount;

public class StreamingSweDataArray extends SweAbstractDataComponent {

        /**
         * swe:values<br />
         * Each list entry represents one block, a list of tokens.<br />
         * Atm, this implementation using java.lang.String to represent each token.
         */
        private StreamingValue values;

        /**
         * swe:elementType
         */
        private SweAbstractDataComponent elementType;

        /**
         * 
         */
        private SweAbstractEncoding encoding;

        private SweCount elementCount;

        /**
         * @return the values
         */
        public StreamingValue getValues() {
            return values;
        }

        /**
         * 
         * @param values
         *            the values to set
         * @return This SweDataArray
         */
        public StreamingSweDataArray setValues(final StreamingValue values) {
            this.values = values;
            return this;
        }

        /**
         * @return the elementType
         */
        public SweAbstractDataComponent getElementType() {
            return elementType;
        }

        /**
         * @param elementType
         *            the elementType to set
         * @return This SweDataArray
         */
        public StreamingSweDataArray setElementType(final SweAbstractDataComponent elementType) {
            this.elementType = elementType;
            return this;
        }

        public SweCount getElementCount() {
            return new SweCount();
        }

        public SweAbstractEncoding getEncoding() {
            return encoding;
        }

        public StreamingSweDataArray setEncoding(final SweAbstractEncoding encoding) {
            this.encoding = encoding;
            return this;
        }

        /**
         * @return <tt>true</tt>, if the values field is set properly
         */
        public boolean isSetValues() {
            try {
                return getValues() != null && getValues().hasNextValue();
            } catch (OwsExceptionReport e) {
                e.printStackTrace();
            }
            return false;
        }


        public boolean isSetElementTyp() {
            return elementType != null;
        }

        public boolean isSetEncoding() {
            return encoding != null;
        }

        public StreamingSweDataArray setElementCount(final SweCount elementCount) {
            this.elementCount = elementCount;
            return this;
        }

        public boolean isSetElementCount() {
            return elementCount != null || isSetValues();
        }

        public boolean isEmpty() {
            return isSetElementTyp() && isSetEncoding() && isSetValues();
        }

        @Override
        public SweDataComponentType getDataComponentType() {
            return SweDataComponentType.DataArray;
        }

}
