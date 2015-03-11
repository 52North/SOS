/**
 * Copyright (C) 2012-2015 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 as published
 * by the Free Software Foundation.
 *
 * If the program is linked with libraries which are licensed under one of
 * the following licenses, the combination of the program with the linked
 * library is not considered a "derivative work" of the program:
 *
 *     - Apache License, version 2.0
 *     - Apache Software License, version 1.0
 *     - GNU Lesser General Public License, version 3
 *     - Mozilla Public License, versions 1.0, 1.1 and 2.0
 *     - Common Development and Distribution License (CDDL), version 1.0
 *
 * Therefore the distribution of the program linked with libraries licensed
 * under the aforementioned licenses, is permitted by the copyright holders
 * if the distribution is compliant with both the GNU General Public
 * License version 2 and the aforementioned licenses.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 */
package org.n52.sos.util.net;

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
