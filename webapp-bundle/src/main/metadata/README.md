## Adding extra metadata fields

Adding extra metadata to each series can be done by creating an extra metadata table.
Apply `src/extension/metadata/create_metadata_table.sql` and add some metadata to it.
Add `/hbm/sos/metadata` to the `seriesSessionFactory` bean in
`WEB-INF/spring/series-database-config.xml` like
```
<property name="mappingLocations">
  <list>
    <value>classpath:hbm/sos/v42/*.hbm.xml</value>
    <value>classpath:hbm/sos/v42/series/*.hbm.xml</value>
    <value>classpath:hbm/sos/metadata/*.hbm.xml</value>
  </list>
</property>
```

Now configure the metadata extension to the Web interface so the data can be accessed.
Open the `api_v1_mvc.xml` add the bean
`org.n52.io.extension.DatabaseMetadataExtension` to the `metadataExtensions`
property of a parameterController.

For example
```
 <bean class="org.n52.web.v1.ctrl.TimeseriesMetadataController" parent="parameterController">
    <property name="serviceParameterService" ref="serviceParameterService" />
    <property name="parameterService" ref="timeseriesService" />
    <property name="metadataExtensions">
        <list merge="true">
            <bean class="org.n52.io.extension.v1.RenderingHintsExtension" />
            <bean class="org.n52.io.extension.v1.StatusIntervalsExtension" />
            <!-- Using DatabaseMetadataExtension requires some preparation work. -->
            <!-- Have a look at the README.md at TBD -->
            <bean class="org.n52.io.extension.DatabaseMetadataExtension" />
        </list>
    </property>
</bean>
```

After restart you should be able to review the extra metadata available in the
`extras` property array of a timeseries. To access them just call
`api/v1/timeseries/:id/extras` and optionally filter by adding query parameter
`fields` (takes a comma-separated list of metadata names).
