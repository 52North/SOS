/**
 * Copyright 2015 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.n52.iceland.service.profile;

import java.util.HashMap;
import java.util.Map;

/**
 * @since 4.0.0
 * 
 */
public class DefaultProfileHandler implements ProfileHandler {

    private Profile activeProfile;

    private Map<String, Profile> availableProfiles = new HashMap<String, Profile>(1);

    public DefaultProfileHandler() {
        activeProfile = new DefaultProfile();
        availableProfiles.put(activeProfile.getIdentifier(), activeProfile);
    }

    @Override
    public Profile getActiveProfile() {
        return activeProfile;
    }

    @Override
    public Map<String, Profile> getAvailableProfiles() {
        return availableProfiles;
    }

    @Override
    public boolean isSetActiveProfile() {
        return activeProfile != null;
    }

}
