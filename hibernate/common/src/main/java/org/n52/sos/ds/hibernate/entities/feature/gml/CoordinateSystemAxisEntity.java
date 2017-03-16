package org.n52.sos.ds.hibernate.entities.feature.gml;

import org.n52.sos.ds.hibernate.entities.Codespace;
import org.n52.sos.ds.hibernate.entities.Unit;
import org.n52.sos.ds.hibernate.entities.feature.ReferenceEntity;

import com.google.common.base.Strings;

public class CoordinateSystemAxisEntity extends ReferenceEntity {

    private String remarks;
    private String axisAbbrev;
    private Codespace axisAbbrevCodespace;
    private String axisDirection;
    private Codespace axisDirectionCodespace;
    private Double minimumValue;
    private Double maximumValue;
    private String rangeMeaning;
    private Codespace rangeMeaningCodespace;
    private Unit uom;

    /**
     * @return the remarks
     */
    public String getRemarks() {
        return remarks;
    }

    /**
     * @param remarks
     *            the remarks to set
     */
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
    
    public boolean isSetRemarks() {
        return !Strings.isNullOrEmpty(getRemarks());
    }

    /**
     * @return the axisAbbrev
     */
    public String getAxisAbbrev() {
        return axisAbbrev;
    }

    /**
     * @param axisAbbrev
     *            the axisAbbrev to set
     */
    public void setAxisAbbrev(String axisAbbrev) {
        this.axisAbbrev = axisAbbrev;
    }

    /**
     * @return the axisAbbrevCodespace
     */
    public Codespace getCodespaceAxisAbbrev() {
        return axisAbbrevCodespace;
    }

    /**
     * @param axisAbbrevCodespace
     *            the axisAbbrevCodespace to set
     */
    public void setCodespaceAxisAbbrev(Codespace axisAbbrevCodespace) {
        this.axisAbbrevCodespace = axisAbbrevCodespace;
    }

    public boolean isSetCodespaceAxisAbbrev() {
        return getCodespaceAxisAbbrev() != null && getCodespaceAxisAbbrev().isSetCodespace();
    }

    /**
     * @return the axisDirection
     */
    public String getAxisDirection() {
        return axisDirection;
    }

    /**
     * @param axisDirection
     *            the axisDirection to set
     */
    public void setAxisDirection(String axisDirection) {
        this.axisDirection = axisDirection;
    }

    /**
     * @return the axisDirectionCodespace
     */
    public Codespace getCodespaceAxisDirection() {
        return axisDirectionCodespace;
    }

    /**
     * @param axisDirectionCodespace
     *            the axisDirectionCodespace to set
     */
    public void setCodespaceAxisDirection(Codespace axisDirectionCodespace) {
        this.axisDirectionCodespace = axisDirectionCodespace;
    }

    public boolean isSetCodespaceAxisDirection() {
        return getCodespaceAxisDirection() != null && getCodespaceAxisDirection().isSetCodespace();
    }

    /**
     * @return the minimumValue
     */
    public Double getMinimumValue() {
        return minimumValue;
    }

    /**
     * @param minimumValue
     *            the minimumValue to set
     */
    public void setMinimumValue(double minimumValue) {
        this.minimumValue = minimumValue;
    }

    public boolean isSetMinimumValue() {
        return getMaximumValue() != null;
    }

    /**
     * @return the maximumValue
     */
    public Double getMaximumValue() {
        return maximumValue;
    }

    /**
     * @param maximumValue
     *            the maximumValue to set
     */
    public void setMaximumValue(double maximumValue) {
        this.maximumValue = maximumValue;
    }

    public boolean isSetMaximumValue() {
        return getMaximumValue() != null;
    }

    /**
     * @return the rangeMeaning
     */
    public String getRangeMeaning() {
        return rangeMeaning;
    }

    /**
     * @param rangeMeaning
     *            the rangeMeaning to set
     */
    public void setRangeMeaning(String rangeMeaning) {
        this.rangeMeaning = rangeMeaning;
    }
    
    public boolean isSetRangeMeaning() {
        return !Strings.isNullOrEmpty(getRangeMeaning());
    }

    /**
     * @return the rangeMeaningCodespace
     */
    public Codespace getCodespaceRangeMeaning() {
        return rangeMeaningCodespace;
    }

    /**
     * @param rangeMeaningCodespace
     *            the rangeMeaningCodespace to set
     */
    public void setCodespaceRangeMeaning(Codespace rangeMeaningCodespace) {
        this.rangeMeaningCodespace = rangeMeaningCodespace;
    }

    public boolean isSetCodespaceRangeMeaning() {
        return getCodespaceRangeMeaning() != null && getCodespaceRangeMeaning().isSetCodespace();
    }

    /**
     * @return the uom
     */
    public Unit getUom() {
        return uom;
    }

    /**
     * @param uom
     *            the uom to set
     */
    public void setUom(Unit uom) {
        this.uom = uom;
    }
}
