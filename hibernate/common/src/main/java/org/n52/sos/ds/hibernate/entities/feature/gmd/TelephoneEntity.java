package org.n52.sos.ds.hibernate.entities.feature.gmd;

import java.util.Set;

public class TelephoneEntity extends AbstractCiEntity {

    private Set<String> voice;
    private Set<String> facsimile;

    /**
     * @return the voice
     */
    public Set<String> getVoice() {
        return voice;
    }

    /**
     * @param voice
     *            the voice to set
     */
    public void setVoice(Set<String> voice) {
        this.voice = voice;
    }

    public boolean hasVoice() {
        return getVoice() != null && !getVoice().isEmpty();
    }
    
    /**
     * @return the facsimile
     */
    public Set<String> getFacsimile() {
        return facsimile;
    }

    /**
     * @param facsimile
     *            the facsimile to set
     */
    public void setFacsimile(Set<String> facsimile) {
        this.facsimile = facsimile;
    }
    
    public boolean hasFacsimile() {
        return getFacsimile() != null && !getFacsimile().isEmpty();
    }
}
