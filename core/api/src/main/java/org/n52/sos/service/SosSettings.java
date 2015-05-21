package org.n52.sos.service;

import java.util.Collections;
import java.util.Set;

import org.n52.iceland.config.SettingDefinition;
import org.n52.iceland.config.SettingDefinitionGroup;
import org.n52.iceland.config.SettingDefinitionProvider;
import org.n52.iceland.config.settings.BooleanSettingDefinition;
import org.n52.iceland.config.settings.StringSettingDefinition;

import com.google.common.collect.Sets;

public class SosSettings implements SettingDefinitionProvider {
    
  public static final String SENSOR_DIRECTORY = "service.sensorDirectory";

  public static final String ENCODE_FULL_CHILDREN_IN_DESCRIBE_SENSOR = "service.encodeFullChildrenInDescribeSensor";

  public static final String MAX_GET_OBSERVATION_RESULTS = "service.maxGetObservationResults";

  public static final String DEREGISTER_JDBC_DRIVER = "service.jdbc.deregister";

  public static final String ADD_OUTPUTS_TO_SENSOR_ML = "service.addOutputsToSensorML";

  public static final String STRICT_SPATIAL_FILTERING_PROFILE = "service.strictSpatialFilteringProfile";

  public static final SettingDefinitionGroup GROUP = new SettingDefinitionGroup().setTitle("SOS").setOrder(2);

  public static final StringSettingDefinition SENSOR_DIRECTORY_DEFINITION =
          new StringSettingDefinition()
                  .setGroup(GROUP)
                  .setOrder(ORDER_7)
                  .setKey(SENSOR_DIRECTORY)
                  .setDefaultValue("/sensors")
                  .setOptional(true)
                  .setTitle("Sensor Directory")
                  .setDescription(
                          "The path to a directory with the sensor descriptions in SensorML format. "
                                  + "It can be either an absolute path (like <code>/home/user/sosconfig/sensors</code>) "
                                  + "or a path relative to the web application classes directory (e.g. <code>WEB-INF/classes/sensors</code>).");

  public static final BooleanSettingDefinition ENCODE_FULL_CHILDREN_IN_DESCRIBE_SENSOR_DEFINITION =
          new BooleanSettingDefinition()
                  .setGroup(GROUP)
                  .setOrder(ORDER_12)
                  .setKey(ENCODE_FULL_CHILDREN_IN_DESCRIBE_SENSOR)
                  .setDefaultValue(true)
                  .setTitle("Encode full for child procedure SensorML in parent DescribeSensor responses")
                  .setDescription(
                          "Whether to encode full SensorML for each child procedures in a DescribeSensor response for a parent procedure.");

  public static final BooleanSettingDefinition DEREGISTER_JDBC_DRIVER_DEFINITION =
          new BooleanSettingDefinition()
                  .setGroup(GROUP)
                  .setOrder(ORDER_13)
                  .setKey(DEREGISTER_JDBC_DRIVER)
                  .setDefaultValue(true)
                  .setTitle("Deregister JDBC driver")
                  .setDescription(
                          "Should the service deregister all used JDBC driver (SQLite, PostgreSQL or H2) during shutdown process.");

  public static final BooleanSettingDefinition ADD_OUTPUTS_TO_SENSOR_ML_DEFINITION =
          new BooleanSettingDefinition()
                  .setGroup(GROUP)
                  .setOrder(ORDER_14)
                  .setKey(ADD_OUTPUTS_TO_SENSOR_ML)
                  .setDefaultValue(true)
                  .setTitle("Add outputs to DescribeSensor SensorML responses")
                  .setDescription(
                          "Whether to query example observations and dynamically add outputs to DescribeSensor SensorML responses.");

  public static final BooleanSettingDefinition STRICT_SPATIAL_FILTERING_PROFILE_DEFINITION =
          new BooleanSettingDefinition()
                  .setGroup(GROUP)
                  .setOrder(ORDER_15)
                  .setKey(STRICT_SPATIAL_FILTERING_PROFILE)
                  .setDefaultValue(false)
                  .setTitle("Should this SOS support strict Spatial Filtering Profile?")
                  .setDescription(
                          "Whether the SOS should support strict SOS 2.0 Spatial Filtering Profile. That means each observation should contain a om:parameter with sampling geometry. Else the SOS allows observations without om:parameter with sampling geometry!");

  private static final Set<SettingDefinition<?, ?>> DEFINITIONS = Sets.<SettingDefinition<?, ?>> newHashSet(
          SENSOR_DIRECTORY_DEFINITION, 
          ENCODE_FULL_CHILDREN_IN_DESCRIBE_SENSOR_DEFINITION, DEREGISTER_JDBC_DRIVER_DEFINITION,
          ADD_OUTPUTS_TO_SENSOR_ML_DEFINITION, STRICT_SPATIAL_FILTERING_PROFILE_DEFINITION);

  @Override
  public Set<SettingDefinition<?, ?>> getSettingDefinitions() {
      return Collections.unmodifiableSet(DEFINITIONS);
  }

}
