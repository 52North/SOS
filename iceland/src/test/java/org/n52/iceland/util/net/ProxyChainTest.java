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
package org.n52.iceland.util.net;


import static org.junit.Assert.assertEquals;

import org.junit.Test;


public class ProxyChainTest {
    
    private String ip = "192.168.52.123";
            
    private String port = ":50684";
    
    private String ipPort = ip + ":" + port;
    
    @Test 
    public void shouldHandleIp() {
        assertEquals("192.168.52.123", ProxyChain.getIPAddress(ip).asString());
     }
    
    @Test 
    public void shouldHandleIpWithPort() {
        assertEquals("192.168.52.123", ProxyChain.getIPAddress(ipPort).asString());
     }

}