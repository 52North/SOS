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

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Range;
import com.google.common.primitives.Ints;

/**
 * Representation of an IPv4 address range based on an address and a subnet
 * mask.
 *
 * @author Christian Autermann <c.autermann@52north.org>
 */
public class IPAddressRange implements Predicate<IPAddress> {
    private static final int CIDR_MAX = 32;
    private static final int CIDR_MIN = 0;
    private final IPAddress address;
    private final IPAddress mask;

    /**
     * Creates a new address range from its string representation. This can be
     * either a CIDR or subnet notation.
     * <br/>
     * Examples:
     * <pre>
     * 192.168.1.1/24
     * 192.168.1.1/255.255.255.0
     * </pre>
     *
     * @param string the string representation
     */
    public IPAddressRange(String string) {
        Preconditions.checkNotNull(string);
        final String[] split = string.split("/", 2);
        Preconditions.checkArgument(split.length == 2,
                                    "Not a valid CIDR address!");
        address = new IPAddress(split[0]);
        final Integer cidr = Ints.tryParse(split[1]);
        if (cidr != null) {
            Preconditions.checkArgument(cidr >= CIDR_MIN && cidr <= CIDR_MAX,
                                        "Not a valid CIDR address!");
            mask = new IPAddress(-1 << (CIDR_MAX - cidr));
        } else {
            mask = new IPAddress(split[1]);
        }
    }

    /**
     * Creates a new address range from an address an a subnet mask.
     *
     * @param address the address
     * @param mask    the subnet mask
     */
    public IPAddressRange(IPAddress address, IPAddress mask) {
        this.address = checkNotNull(address);
        this.mask = checkNotNull(mask);
    }

    /**
     * @return the IP address
     */
    public IPAddress getAddress() {
        return address;
    }

    /**
     * @return the subnet mask
     */
    public IPAddress getSubnetMask() {
        return mask;
    }

    /**
     * @return the highest IP address in this range
     */
    public IPAddress getHigh() {
        return new IPAddress(getLow().asInt() + (~getSubnetMask().asInt()));
    }

    /**
     * @return the lowest IP address in this range
     */
    public IPAddress getLow() {
        return new IPAddress(getAddress().asInt() & getSubnetMask().asInt());
    }

    /**
     * Checks if a given IP address is in this range.
     *
     * @param ip the address
     *
     * @return whether this range contains the address
     */
    public boolean contains(IPAddress ip) {
        return ip.compareTo(getLow()) >= 0 &&
               ip.compareTo(getHigh()) <= 0;
    }

    /**
     * @return this address range as a {@link Range}
     */
    public Range<IPAddress> asRange() {
        return Range.closed(getLow(), getHigh());
    }

    @Override
    public boolean apply(IPAddress input) {
        return contains(input);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getAddress(), getSubnetMask());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof IPAddressRange) {
            IPAddressRange other = (IPAddressRange) obj;
            return Objects.equal(getAddress(), other.getAddress()) &&
                   Objects.equal(getSubnetMask(), other.getSubnetMask());
        }
        return false;
    }

    @Override
    public String toString() {
        return getAddress() + "/" + getSubnetMask();
    }
}
