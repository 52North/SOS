package org.n52.sos.iso.gmd;

import java.util.ArrayList;
import java.util.List;

import org.n52.sos.iso.gco.AbstractObject;
import org.n52.sos.util.CollectionHelper;

import com.google.common.base.Strings;

public class EXExtent extends AbstractObject {
    
    private String description;
    private List<Object> exGeographicalExtent = new ArrayList<>();
    private List<Object> exTemporalExtent = new ArrayList<>();
    private List<EXVerticalExtent> exVerticalExtent = new ArrayList<>();
    
    public String getDescription() {
        return description;
    }
    
    public EXExtent setDescription(String description) {
        this.description = description;
        return this;
    }
    
    public boolean hasDescription() {
        return !Strings.isNullOrEmpty(getDescription());
    }
    
    public List<Object> getExGeographicalExtent() {
        return exGeographicalExtent;
    }
    
    public EXExtent setExGeographicalExtent(List<Object> exGeographicalExtent) {
        this.exGeographicalExtent.clear();
        if (!CollectionHelper.nullEmptyOrContainsOnlyNulls(exGeographicalExtent)) {
            this.exGeographicalExtent.addAll(exGeographicalExtent);
        }
        return this;
    }
    
    public EXExtent addExGeographicalExtent(List<Object> exGeographicalExtent) {
        if (!CollectionHelper.nullEmptyOrContainsOnlyNulls(exGeographicalExtent)) {
            this.exGeographicalExtent.addAll(exGeographicalExtent);
        }
        return this;
    }
    
    public EXExtent addExGeographicalExtent(Object exGeographicalExtent) {
        if (exGeographicalExtent != null) {
            this.exGeographicalExtent.add(exGeographicalExtent);
        }
        return this;
    }
    
    public List<Object> getExTemporalExtent() {
        return exTemporalExtent;
    }
    
    public EXExtent setExTemporalExtent(List<Object> exTemporalExtent) {
        this.exTemporalExtent.clear();
        if (!CollectionHelper.nullEmptyOrContainsOnlyNulls(exTemporalExtent)) {
            this.exTemporalExtent.addAll(exTemporalExtent);
        }
        return this;
    }
    
    public EXExtent addExTemporalExtent(List<Object> exTemporalExtent) {
        if (!CollectionHelper.nullEmptyOrContainsOnlyNulls(exTemporalExtent)) {
            this.exTemporalExtent.addAll(exTemporalExtent);
        }
        return this;
    }
    
    public EXExtent addExTemporalExtent(Object exTemporalExtent) {
        if (exTemporalExtent != null) {
            this.exTemporalExtent.add(exTemporalExtent);
        }
        return this;
    }
    
    public List<EXVerticalExtent> getExVerticalExtent() {
        return exVerticalExtent;
    }
    
    public EXExtent setExVerticalExtent(List<EXVerticalExtent> exVerticalExtent) {
        this.exVerticalExtent.clear();
        if (!CollectionHelper.nullEmptyOrContainsOnlyNulls(exVerticalExtent)) {
            this.exVerticalExtent.addAll(exVerticalExtent);
        }
        return this;
    }
    
    public EXExtent addExVerticalExtent(List<EXVerticalExtent> exVerticalExtent) {
        if (!CollectionHelper.nullEmptyOrContainsOnlyNulls(exVerticalExtent)) {
            this.exVerticalExtent.addAll(exVerticalExtent);
        }
        return this;
    }
    
    public EXExtent addExVerticalExtent(EXVerticalExtent exVerticalExtent) {
        if (exVerticalExtent != null) {
            this.exVerticalExtent.add(exVerticalExtent);
        }
        return this;
    }

}
