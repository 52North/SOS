package org.n52.sos.ds.hibernate.util;

import java.util.Map;

import org.n52.sos.service.profile.Profile;
import org.n52.sos.service.profile.ProfileHandler;
import org.n52.sos.service.profile.Profiles;

public class ProfileHanlderMock implements ProfileHandler, Profiles {

    @Override
    public void init() {
        // TODO Auto-generated method stub

    }

    @Override
    public Profile getActiveProfile() {
        return createSosProfile();
    }

    @Override
    public Map<String, Profile> getAvailableProfiles() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isSetActiveProfile() {
        return true;
    }

    @Override
    public void activateProfile(String identifier) {
        // TODO Auto-generated method stub

    }

    @Override
    public void reloadProfiles() {
        // TODO Auto-generated method stub
    }

}
