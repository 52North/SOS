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

import java.net.Inet4Address;

import com.google.common.base.Objects;
import com.google.common.net.InetAddresses;
import com.google.common.primitives.Ints;

/**
 * Encapsulation of an IPv4 address.
 *
 * @author Christian Autermann <c.autermann@52north.org>
 */
public class IPAddress implements Comparable<IPAddress> {
    private final int address;

    /**
     * Creates a new IPAddress from an 32-Bit integer.
     *
     * @param address the address
     */
    public IPAddress(int address) {
        this.address = address;
    }

    /**
     * Creates a new IPAddress from an four element byte array.
     *
     * @param address the address
     */
    public IPAddress(byte[] address) {
        this(Ints.fromByteArray(address));
    }

    /**
     * Creates a new IPAddress from its string representation.
     *
     * @param address the address
     */
    public IPAddress(String address) {
        this(parse(address));
    }

    /**
     * Creates a new IPAddress from an {@link Inet4Address}.
     *
     * @param address the address
     */
    public IPAddress(Inet4Address address) {
        this(address.getAddress());
    }

    /**
     * @return the IP address as an 32-bit integer
     */
    public int asInt() {
        return this.address;
    }

    /**
     * @return the IP address as an {@code Inet4Address}
     */
    public Inet4Address asInetAddress() {
        return InetAddresses.fromInteger(this.address);
    }

    /**
     * @return the IP address as an 4 element byte array.
     */
    public byte[] asByteArray() {
        return Ints.toByteArray(this.address);
    }

    /**
     * @return the IP address as a string
     */
    public String asString() {
        return asInetAddress().getHostAddress();
    }

    @Override
    public int compareTo(IPAddress o) {
        return Ints.compare(this.address, checkNotNull(o).asInt());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.address);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof IPAddress) {
            IPAddress other = (IPAddress) obj;
            return this.address == other.asInt();
        }
        return false;
    }

    @Override
    public String toString() {
        return asString();
    }

    private static Inet4Address parse(String address) {
        try {
            return (Inet4Address) InetAddresses.forString(address);
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("IPv6 addresses are not supported.", e);
        }
    }
}
