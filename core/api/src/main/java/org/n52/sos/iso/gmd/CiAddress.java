package org.n52.sos.iso.gmd;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.common.base.Strings;

/**
 * Internal representation of the ISO GMD Address.
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.4.0
 *
 */
public class CiAddress extends AbstractObject {
    
    private List<String> deliveryPoints = new ArrayList<>();
    private String city;
    private String administrativeArea;
    private String postalCode;
    private String country;
    private List<String> electronicMailAddresses = new ArrayList<>();

    /**
     * @return the deliveryPoints
     */
    public List<String> getDeliveryPoints() {
        return deliveryPoints;
    }

    /**
     * @param deliveryPoints
     *            the deliveryPoints to set
     */
    public CiAddress setDeliveryPoints(Collection<String> deliveryPoints) {
        if (electronicMailAddresses != null) {
            this.deliveryPoints.addAll(deliveryPoints);
        }
        return this;
    }
    
    /**
     * @param deliveryPoints
     *            the deliveryPoints to add
     */
    public CiAddress addDeliveryPoints(String deliveryPoints) {
        if (electronicMailAddresses != null) {
            this.deliveryPoints.add(deliveryPoints);
        }
        return this;
    }
    
    public boolean hasDeliveryPoints() {
        return getDeliveryPoints() != null && !getDeliveryPoints().isEmpty();
    }

    /**
     * @return the city
     */
    public String getCity() {
        return city;
    }

    /**
     * @param city
     *            the city to set
     */
    public CiAddress setCity(String city) {
        this.city = city;
        return this;
    }
    
    public boolean isSetCity() {
        return !Strings.isNullOrEmpty(getCity());
    }

    /**
     * @return the administrativeArea
     */
    public String getAdministrativeArea() {
        return administrativeArea;
    }

    /**
     * @param administrativeArea
     *            the administrativeArea to set
     */
    public CiAddress setAdministrativeArea(String administrativeArea) {
        this.administrativeArea = administrativeArea;
        return this;
    }

    public boolean isSetAdministrativeArea() {
        return !Strings.isNullOrEmpty(getCity());
    }
    
    /**
     * @return the postalCode
     */
    public String getPostalCode() {
        return postalCode;
    }

    /**
     * @param postalCode
     *            the postalCode to set
     */
    public CiAddress setPostalCode(String postalCode) {
        this.postalCode = postalCode;
        return this;
    }
    
    public boolean isSetPostalCode() {
        return !Strings.isNullOrEmpty(getPostalCode());
    }

    /**
     * @return the country
     */
    public String getCountry() {
        return country;
    }

    /**
     * @param country
     *            the country to set
     */
    public CiAddress setCountry(String country) {
        this.country = country;
        return this;
    }
    
    public boolean isSetCountry() {
        return !Strings.isNullOrEmpty(getCountry());
    }

    /**
     * @return the electronicMailAddresses
     */
    public List<String> getElectronicMailAddresses() {
        return electronicMailAddresses;
    }

    /**
     * @param electronicMailAddresses
     *            the electronicMailAddresses to set
     */
    public CiAddress setElectronicMailAddresses(Collection<String> electronicMailAddresses) {
        this.electronicMailAddresses.clear();
        if (electronicMailAddresses != null) {
            this.electronicMailAddresses.addAll(electronicMailAddresses);
        }
        return this;
    }
    
    /**
     * @param electronicMailAddresses
     *            the electronicMailAddresses to add
     */
    public CiAddress addElectronicMailAddresses(String electronicMailAddresses) {
        if (electronicMailAddresses != null) {
            this.electronicMailAddresses.add(electronicMailAddresses);
        }
        return this;
    }
    
    public boolean hasElectronicMailAddresses() {
        return getElectronicMailAddresses() != null && !getElectronicMailAddresses().isEmpty();
    }

}
