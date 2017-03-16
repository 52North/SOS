package org.n52.sos.iso.gmd;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.n52.sos.util.CollectionHelper;

public class CiTelephone extends AbstractObject {

    private List<String> voice = new ArrayList<>();
    private List<String> facsimile = new ArrayList<>();
    
    public boolean isSetVoice() {
        return !CollectionHelper.nullEmptyOrContainsOnlyNulls(voice);
    }

    public List<String> getVoice() {
        return voice;
    }

    public CiTelephone setVoice(final Collection<String> voice) {
        voice.clear();
        if (voice != null) {
            this.voice.addAll(voice);
        }
        return this;
    }

    public CiTelephone addVoice(final String voice) {
        if (voice != null) {
            this.voice.add(voice);
        }
        return this;
    }

    public boolean isSetFacsimile() {
        return !CollectionHelper.nullEmptyOrContainsOnlyNulls(facsimile);
    }

    public List<String> getFacsimile() {
        return facsimile;
    }

    public CiTelephone addFacsimile(final String facsimile) {
        if (facsimile != null) {
            this.facsimile.add(facsimile);
        }
        return this;
    }

    public CiTelephone setFacsimile(final Collection<String> facsimile) {
        this.facsimile.clear();
        if (facsimile != null) {
            this.facsimile.addAll(facsimile);
        }
        return this;
    }

}
