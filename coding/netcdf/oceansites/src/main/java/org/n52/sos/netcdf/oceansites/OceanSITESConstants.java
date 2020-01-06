/**
 * Copyright (C) 2012-2020 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.netcdf.oceansites;

import org.n52.sos.ogc.OGCConstants;
import org.n52.sos.util.http.MediaType;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableListMultimap;

/**
 * Constants interface for OceanSITES specific constants.
 * 
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 4.4.0
 *
 */
public interface OceanSITESConstants {

    String OCEANSITES = "OceanSITES";

    String NAMING_AUTHORITY_TEXT = OCEANSITES;

    String OCEANSITES_VERSION = OCEANSITES + "-1.3";

    String ACCD_VERSION = "ACCD-1.2";

    public static MediaType CONTENT_TYPE_NETCDF_OCEANSITES = new MediaType("application", "netcdf", "profile",
            OCEANSITES);

    public static MediaType CONTENT_TYPE_NETCDF_3_OCEANSITES = new MediaType("application", "netcdf",
            ImmutableListMultimap.of("version", "3", "profile", OCEANSITES));

    public static MediaType CONTENT_TYPE_NETCDF_4_OCEANSITES = new MediaType("application", "netcdf",
            ImmutableListMultimap.of("version", "4", "profile", OCEANSITES));

    public static MediaType CONTENT_TYPE_NETCDF_ZIP_OCEANSITES = new MediaType("application", "zip",
            ImmutableListMultimap.of("subtype", "netcdf", "profile", OCEANSITES));

    public static MediaType CONTENT_TYPE_NETCDF_3_ZIP_OCEANSITES = new MediaType("application", "zip",
            ImmutableListMultimap.of("subtype", "netcdf", "version", "3", "profile", OCEANSITES));

    public static MediaType CONTENT_TYPE_NETCDF_4_ZIP_OCEANSITES = new MediaType("application", "zip",
            ImmutableListMultimap.of("subtype", "netcdf", "version", "4", "profile", OCEANSITES));

    String EPSG_4326 = OGCConstants.URN_DEF_CRS_EPSG + "4326";

    String EPSG_5831 = OGCConstants.URN_DEF_CRS_EPSG + "5831";

    String EPSG_5829 = OGCConstants.URN_DEF_CRS_EPSG + "5829";

    String EPSG_DEPTH = EPSG_5831;

    String EPSG_HEIGHT = EPSG_5829;

    String EPSG_REFERENCE = "WGS84";

    String FILL_VALUE = "";

    //
    String NERC_VOCAB_P02_PREFIX = "http://vocab.nerc.ac.uk/collection/P02/current/";

    String KEYWORDS_VOCABULARY_TEXT = "SeaDataNet Parameter Discovery Vocabulary";

    /*
     * Update interval for the file, in ISO 8601 Interval format: PnYnMnDTnHnM
     * where elements that are 0 may be omitted. Use “void” for data that are
     * not updated on a schedule. Used by inventory software. (GDAC)
     */
    String UPDATE_INTERVAL_TEXT = "void";

    /*
     * Discover and identification
     */

    /**
     * REQUIRED</br>
     * 
     * example: site_code=”CIS” (OceanSITES specific)</br> note: Name of the
     * site within OceanSITES project. The site codes are available on GDAC ftp
     * servers. Required (GDAC)
     */
    String SITE_CODE = "site_code";

    /**
     * REQUIRED</br>
     * 
     * example: platform_code=”CIS-1” (OceanSITES specific)</br> note: The
     * unique platform code, assigned by an OceanSITES project. Required. (GDAC)
     */
    String PLATFORM_CODE = "platform_code";

    /**
     * REQUIRED</br>
     * 
     * example: data_mode=”R” (OceanSITES specific)</br> note: Indicates if the
     * file contains real-time, provisional or delayed-mode data. The list of
     * valid data modes is in {@link DataMode}. (GDAC)
     */
    String DATA_MODE = "data_mode";

    /**
     * OPTIONAL</br>
     * 
     * example: wmo_platform_code=”48409” (OceanSITES specific)</br> note: WMO
     * (World Meteorological Organization) identifier. This platform number is
     * unique within the OceanSITES project.
     */
    String WMO_PLATFORM_CODE = "wmo_platform_code";

    /**
     * OPTIONAL</br>
     * 
     * example: array=”TAO” (OceanSITES specific)</br> note: A grouping of sites
     * based on a common and identified scientific question, or on a common
     * geographic location.
     */
    String ARRAY = "array";

    /**
     * OPTIONAL</br>
     * 
     * example: network=”EuroSITES” (OceanSITES specific)</br> note: A grouping
     * of sites based on common shore-based logistics or infrastructure.
     */
    String NETWORK = "network";

    /**
     * OPTIONAL</br>
     * 
     * example: principal_investigator=”Alice Juarez”</br> note: Name of the
     * person responsible for the project that produced the data contained in
     * the file.
     */
    String PRINCIPAL_INVESTIGATOR = "principal_investigator";

    /**
     * OPTIONAL</br>
     * 
     * example: principal_investigator_email =”AJuarez AT whoi.edu”</br> note:
     * Email address of the project lead for the project that produced the data
     * contained in the file.
     */
    String PRINCIPAL_INVESTIGATOR_EMAIL = "principal_investigator_email";

    /**
     * OPTIONAL</br>
     * 
     * example: principal_investigator_url=” whoi.edu/profile/AJuarez”</br>
     * note: URL with information about the project lead.
     */
    String PRINCIPAL_INVESTIGATOR_URL = "principal_investigator_url";

    /*
     * Geo-spatial-temporal
     */

    /**
     * REQUIRED</br>
     * 
     * example: data_type=”OceanSITES time-series data”</br> note: From
     * {@link DataType}: OceanSITES specific. (GDAC)
     */
    String DATA_TYPE = "data_type";

    /**
     * OPTIONAL</br>
     * 
     * example: area=”North Atlantic Ocean” (OceanSITES specific)</br> note:
     * Geographical coverage. Try to compose of the following:
     * North/Tropical/South Atlantic/Pacific/Indian Ocean, Southern Ocean,
     * Arctic Ocean.
     */
    String AREA = "area";

    /*
     * OPTIONAL
     */

    /*
     * Conventions used
     */

    /**
     * REQUIRED</br>
     * 
     * example: format_version=”1.3” (OceanSITES specific)</br> note: OceanSITES
     * format version; may be 1.1, 1.2, 1.3. (GDAC)
     */
    String FORMAT_VERSION = "format_version";

    String FORMAT_VERSION_DEFAULT_TEXT = "1.3";

    /**
     * OPTIONAL</br>
     * 
     * example: netcdf_version=”3.5” (OceanSITES specific)</br> note: NetCDF
     * version used for the data set
     */
    String NETCDF_VERSION = "netcdf_version";

    /*
     * Publication information
     */

    /**
     * REQUIRED</br>
     * 
     * example: update_interval=”PT12H” (OceanSITES specific)</br> note: Update
     * interval for the file, in ISO 8601 Interval format: PnYnMnDTnHnM where
     * elements that are 0 may be omitted. Use “void” for data that are not
     * updated on a schedule. Used by inventory software. (GDAC)
     */
    String UPDATE_INTERVAL = "update_interval";

    /**
     * OPTIONAL</br>
     * 
     * example: data_assembly_center=”EUROSITES” (OceanSITES specific)</br>
     * note: Data Assembly Center (DAC) in charge of this data file. The
     * data_assembly_center are listed in {@link DACC}.
     */
    String DAC = "data_assembly_center";

    /**
     * OPTIONAL</br>
     * 
     * example: citation={@link CITATION_DEFAULT_TEXT} (OceanSITES
     * specific)</br> note: The citation to be used in publications using the
     * dataset; should include a reference to OceanSITES but may contain any
     * other text deemed appropriate by the PI and DAC.
     */
    String CITATION = "citation";

    /**
     * Default text for citation
     */
    String CITATION_DEFAULT_TEXT =
            "These data were collected and made freely available by the OceanSITES project and the national programs that contribute to it.";

    /**
     * Default text for license</br> note: A statement describing the data
     * distribution policy; it may be a project- or DAC-specific statement, but
     * must allow free use of data. OceanSITES has adopted the CLIVAR data
     * policy, which explicitly calls for free and unrestricted data exchange.
     * Details at: http://www.clivar.org/data/data_policy.php (ACDD)
     */
    String LICENSE_DEFAULT_TEXT =
            "Follows CLIVAR (Climate Varibility and Predictability) standards, cf. http://www.clivar.org/data/data_policy.php. Data available free of charge. User assumes all risk for use of data. User must display citation in any publication or product using data. User must contact PI prior to any commercial use of data.";

    /*
     * Provenance
     */

    /**
     * OPTIONAL</br>
     * 
     * example: processing_level =” Data verified against model or other
     * contextual information” (OceanSITES specific)</br> note: Level of
     * processing and quality control applied to data. Preferred values are
     * listed in {@link ProcessingLevel}
     */
    String PROCESSING_LEVEL = "processing_level";

    /**
     * OPTIONAL</br>
     * 
     * example: QC_indicator =”excellent” (OceanSITES specific)</br> note: A
     * value valid for the whole dataset, one of {@link QCIndicator}
     */
    String QC_INDICATOR = "QC_indicator";

    String WMO_PLATFORM_CODE_DEFINITION = "http://www.nexosproject.eu/dictionary/definitions.html#WMO_ID";

    String ACKNOWLEDGEMENT_DEFINITION = "http://www.nexosproject.eu/dictionary/definitions.html#acknowledgment";

    String PROJECT_DEFINITION = "http://www.nexosproject.eu/dictionary/definitions.html#PROJECT_ID";

    String ARRAY_DEFINITION = "http://www.nexosproject.eu/dictionary/definitions.html#ARRAY_ID";

    String NETWORK_DEFINITION = "http://www.nexosproject.eu/dictionary/definitions.html#NETWORK_ID";

    String PROJECT_DEFAULT = "";

    String ACKNOWLEDGEMENT_DEFAULT = "";

    String SITE_CODE_DEFINITION = "http://www.nexosproject.eu/dictionary/definitions.html#SITE_ID";

    String PLATFORM_CODE_DEFINITION = "http://www.nexosproject.eu/dictionary/definitions.html#PLATFORM_ID";

    String DATA_MODE_DEFINITION = "http://www.nexosproject.eu/dictionary/definitions.html#DATA_MODE";
    
    String AREA_DEFINITION = "http://www.nexosproject.eu/dictionary/definitions.html#AREA";

    String REFERENCE = "reference";

    String HEIGHT_DEPTH_REFERENCE_DEFAULT = "sea_level";

    String COORDINATE_REFERENCE_FRAME = "coordinate_reference_frame";

    String UNITS_TIME = "days since 1950-01-01T00:00:00Z";

    String REFERENCES_DEFAULT_TEXT = "http://www.oceansites.org";

    enum Coordinates {
        TIME, DEPTH, HEIGHT, LATITUDE, LONGITUDE
    }

    /**
     * relative to sea floor up - atmospheric down - oceanic
     * 
     * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
     * @since 4.4.0
     *
     */
    enum DepthPositive {
        up, down;
    }

    enum DepthReference {
        sea_level, mean_sea_level, mean_lower_low_water, wgs84_geoid;

        public DepthReference getDefault() {
            return sea_level;
        }
    }

    enum QCIndicator {
        UNKNOWN("unknown", "no QC done, no known problems"), EXCELLENT("excellent", "no known problems, some QC done"), PROBABLY_GOOD(
                "probably good", "validation phase"), MIXED("mixed", "some problems, see variable attributes");

        private final String value;

        private final String meaning;

        QCIndicator(String value, String meaning) {
            this.value = value;
            this.meaning = meaning;
        }

        /**
         * @return the name
         */
        public String getValue() {
            return value;
        }

        /**
         * @return the meaning
         */
        public String getMeaning() {
            return meaning;
        }
    }

    enum DataType {
        OS_PROFILE("OceanSITES profile data"), OS_TIME_SERIES("OceanSITES time-series data"), OS_TRAJECTORY(
                "OceanSITES trajectory data");

        private final String type;

        DataType(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }

    }

    enum QCIndicatorValues {
        QCI_0(0, "unknown", "No QC was performed"), QCI_1(1, "good data", "All QC tests passed."), QCI_2(2,
                "probably good data", ""), QCI_3(3, "potentially correctable bad data",
                "These data are not to be used without scientific correction or re-calibration."), QCI_4(4,
                "bad data", "Data have failed one or more tests."), QCI_5(5, "-", "Not used"), QCI_6(6, "-",
                "Not used."), QCI_7(7, "nominal value",
                "Data were not observed but reported. (e.g. instrument target depth.)"), QCI_8(8,
                "interpolated value", "Missing data may be interpolated from neighboring data in space or time."), QCI_9(
                9, "missing value", "This is a fill value;");

        private final int code;

        private final String meaning;

        private final String comment;

        QCIndicatorValues(int code, String meaning, String comment) {
            this.code = code;
            this.meaning = meaning;
            this.comment = comment;
        }

        /**
         * @return the code
         */
        public int getCode() {
            return code;
        }

        /**
         * @return the meaning
         */
        public String getMeaning() {
            return meaning;
        }

        /**
         * @return the comment
         */
        public String getComment() {
            return comment;
        }

        public static QCIndicatorValues from(String v) {
            for (QCIndicatorValues c : QCIndicatorValues.values()) {
                if (c.getCode() == Integer.valueOf(v) || c.getMeaning().equals(v)) {
                    return c;
                }
            }
            throw new IllegalArgumentException(v);
        }

        public static QCIndicatorValues fromCode(int v) {
            for (QCIndicatorValues c : QCIndicatorValues.values()) {
                if (c.getCode() == v) {
                    return c;
                }
            }
            throw new IllegalArgumentException(Integer.toString(v));
        }

        public static QCIndicatorValues fromCode(String v) {
            for (QCIndicatorValues c : QCIndicatorValues.values()) {
                if (c.getCode() == Integer.valueOf(v)) {
                    return c;
                }
            }
            throw new IllegalArgumentException(v);
        }

        public static QCIndicator fromMeaning(String v) {
            for (QCIndicator c : QCIndicator.values()) {
                if (c.getMeaning().equals(v)) {
                    return c;
                }
            }
            throw new IllegalArgumentException(v);
        }

    }

    enum ProcessingLevel {
        RAW_INST_DATA("Raw instrument data"), INST_DATA_CONVERTED_GEOPHYS(
                "Instrument data that has been converted to geophysical values"), POST_RECOVERY_CALIBRATIONS(
                "Post-recovery calibrations have been applied"), DATA_SCALED_CONTEXT_INFO(
                "Data has been scaled using contextual information"), BAD_DATA_REPLACED_NULL(
                "Known bad data has been replaced with null values"), BAD_DATA_REPLACED_SURROUNDING_DATA(
                "Known bad data has been replaced with values based on surrounding data"), RANGES_APPLIED_BAD_DATA_FLAGGED(
                "Ranges applied, bad data flagged"), DATA_INTERPOLATED("Data interpolated"), DATA_MANUALLY_REVIEWED(
                "Data manually reviewed"), DATA_VERIFIED_MODEL_CONTEXTUAL_INFO(
                "Data verified against model or other contextual information"), OTHER_QC_PROCESS(
                "Other QC process applied");

        private final String text;

        ProcessingLevel(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }
    }

    /**
     * R - Real-time data. Data coming from the (typically remote) platform
     * through a communication channel without physical access to the
     * instruments, disassembly or recovery of the platform. Example: for a
     * mooring with a radio communication, this would be data obtained through
     * the radio.</br>
     * 
     * P - Provisional data. Data obtained after instruments have been recovered
     * or serviced; some calibrations or editing may have been done, but the
     * data is not thought to be fully processed. Refer to the history attribute
     * for more detailed information.</br>
     * 
     * D - Delayed-mode data. Data published after all calibrations and quality
     * control procedures have been applied on the internally recorded or best
     * available original data. This is the best possible version of processed
     * data.</br>
     * 
     * M - Mixed. This value is only allowed in the global attribute “data_mode”
     * or in attributes to variables in the form “<PARAM>:DM_indicator”. It
     * indicates that the file contains data in more than one of the above
     * states. In this case, the variable(s) <PARAM>_DM specify which data is in
     * which data mode.</br>
     * 
     * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
     * @since 4.4.0
     *
     */
    enum DataMode {
        R, P, D, M;

    }

    /**
     * Data Assembly Center codes (DACC)
     * 
     * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
     * @since 4.4.0
     *
     */
    enum DACC {
        BERGEN("University Of Bergen Geophysical Institute, NO"), CCHDO("CLIVAR and Carbon Hydographic Office, USA"), CDIAC(
                "Carbon Dioxide Information Analysis Center, USA"), EUROSITES("EuroSites project, EU"), IMOS(
                "Integrated Marine Observing System, AU"), INCOIS(
                "Indian National Centre for Ocean Information Services"), JAMSTEC(
                "Japan Agency for Marine-Earth Science and Technology"), MBARI(
                "Monterey Bay Aquarium Research Institute, USA"), MEDS("Marine Environmental Data Service, Canada"), NDBC(
                "National Data Buoy Center, USA"), NIOZ("Royal Netherlands Institute for Sea Research, NL"), NOCS(
                "National Oceanography Centre, Southampton UK"), PMEL("Pacific Marine Environmental Laboratory, USA"), SIO(
                "Scripps Institution of Oceanography, USA"), UH("University of Hawaii, USA"), WHOI(
                "Woods Hole Oceanographic Institution, USA");

        private final String name;

        DACC(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    enum VariableName {
        AIR("air_temperature", ""), CAPH("air_pressure", ""), CDIR("direction_of_sea_water_velocity", ""), CNDC(
                "sea_water_electrical_conductivity", ""), CSPD("sea_water_speed", ""), DEPTH("depth", ""), DEWT(
                "dew_point_temperature", ""), DOX2(" moles_of_oxygen_per_unit_mass_in_sea_water was dissolved_oxygen",
                ""), DOXY("mass_concentration_of_oxygen_in_sea_water was dissolved_oxygen", ""), DOXY_TEMP(
                "temperature_of_sensor_for_oxygen_in_sea_water", ""), DYNHT("", "dynamic_height"), FLU2("",
                "fluorescence"), HCSP("sea_water_speed", ""), HEAT("", "heat_content"), ISO17("", "isotherm_depth"), LW(
                "surface_downwelling_longwave_flux_in_air", ""), OPBS("", "optical_backscattering_coefficient"), PCO2(
                "surface_partial_pressure_of_carbon_dioxide_in_air", ""), PRES("sea_water_pressure", ""), PSAL(
                "sea_water_practical_salinity", ""), RAIN("rainfall_rate", ""), RAIT("thickness_of_rainfall_amount",
                ""), RELH("relative_humidity", ""), SDFA("surface_downwelling_shortwave_flux_in_air", ""), SRAD(
                "isotropic_shortwave_radiance_in_air", ""), SW("surface_downwelling_shortwave_flux_in_air", ""), TEMP(
                "sea_water_temperature", ""), UCUR("eastward_sea_water_velocity", ""), UWND("eastward_wind", ""), VAVH(
                "sea_surface_wave_significant_height", ""), VAVT("sea_surface_wave_zero_upcrossing_period", ""), VCUR(
                "northward_sea_water_velocity", ""), VDEN("sea_surface_wave_variance_spectral_density", ""), VDIR(
                "sea_surface_wave_from_direction", ""), VWND("northward_wind", ""), WDIR("wind_to_direction", ""), WSPD(
                "wind_speed", "");

        private final String standardName;

        private final String longName;

        private VariableName(String standardName, String longName) {
            this.standardName = standardName;
            this.longName = longName;
        }

        /**
         * @return the standardName
         */
        public String getStandardName() {
            return standardName;
        }

        /**
         * @return the longName
         */
        public String getLongName() {
            return longName;
        }

        public boolean isSetStandardName() {
            return !Strings.isNullOrEmpty(getStandardName());
        }

        public boolean isSetLongName() {
            return !Strings.isNullOrEmpty(getLongName());
        }
    }

    enum SensorMount {
        mounted_on_fixed_structure, mounted_on_surface_buoy, mounted_on_mooring_line, mounted_on_bottom_lander, mounted_on_moored_profiler, mounted_on_glider, mounted_on_shipborne_fixed, mounted_on_shipborne_profiler, mounted_on_seafloor_structure, mounted_on_benthic_node, mounted_on_benthic_crawler, mounted_on_surface_buoy_tether, mounted_on_seafloor_structure_riser, mounted_on_fixed_subsurface_vertical_profiler;

    }

    enum SensorOrientation {
        downward("ADCP measuring currents from surface to bottom."), upward(
                "In-line ADCP measuring currents towards the surface"), horizontal(
                "Optical sensor looking ‘sideways’ from mooring line");

        private final String example;

        SensorOrientation(String example) {
            this.example = example;
        }

        public String getExample() {
            return example;
        }
    }

}
