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
package org.n52.iceland.convert;

public class RequestResponseModifierFacilitator {
    
    private boolean merger = false;
    
    private boolean splitter = false;
    
    private boolean adderRemover = false;
    
    public RequestResponseModifierFacilitator setMerger(boolean merger) {
        this.merger = merger;
        return this;
    }
    
    public boolean isMerger() {
        return merger;
    }
    
    public RequestResponseModifierFacilitator setSplitter(boolean splitter) {
        this.splitter = splitter;
        return this;
    }
    
    public boolean isSplitter() {
        return splitter;
    }
    
    public RequestResponseModifierFacilitator setAdderRemover(boolean adderRemover) {
        this.adderRemover = adderRemover;
        return this;
    }
    
    public boolean isAdderRemover() {
        return adderRemover;
    }
    
}
