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
package org.n52.sos.ds;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk J&uuml;rrens</a>
 * 
 * @since 4.0.0
 */
public class TestDataSqlScriptGenerator {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(TestDataSqlScriptGenerator.class);
	private static final int FEATURE_COUNT_INDEX = 0;
	private static final int SENSOR_COUNT_INDEX = 1;
	private static final int OBSERVATION_COUNT_INDEX = 2;
	private static final String DEFAULT_FILENAME = "../db/generated-test-data.sql";
	private static final int FILENAME_INDEX = 4;
	private static final int X_COORD_INDEX = 0;
	private static final int Y_COORD_INDEX = 1;
	private static final String SQL_INSERT_FEATURE = "SELECT insert_feature_of_interest('test_feature_%s', %s, %s);";
	private static final String SQL_TIMESTAMP_PATTERN = "yyyy-MM-dd HH:mm";
	private static final String SQL_INSERT_SENSOR = "SELECT insert_procedure('http://www.example.org/sensors/%s', '%s', '%s', %s, %s, 0.0, '%s', '%s');";
	private static final String OBSERVATION_TYPE = "Measurement";
	private static final String OBSERVED_PROPERTY_ID = "test_observable_property_1";
	private static final String FEATURE_TYPE = "Point";
	private static final String SQL_INSERT_OFFERING = "SELECT insert_offering('test_offering_%s');";
	private static final String SQL_INSERT_OBSERVATION_CONSTELLATION = "SELECT insert_observation_constellation('%s', 'http://www.example.org/sensors/%s', 'test_offering_%s', '%s');";
	private static final long TIMESTAMP_INCREMENT_IN_MS = 600000l;
	private static final String UNIT_ID = "test_unit_1";
	private static final double RESULT_MIN_VALUE = 0.0;
	private static final double RESULT_MAX_VALUE = 100.0;
	private static final String SQL_INSERT_NUMERIC_OBSERVATION = "SELECT insert_numeric_observation(insert_observation(get_observation_constellation('%s', 'http://www.example.org/sensors/%s', 'test_offering_%s', '%s'), 'test_feature_%s', '%s', '%s'), %s);";
	private static final String HEADER_FILE_NAME = "/generate-test-data-header.sql";
	private static final String FOOTER_FILE_NAME = "/generate-test-data-footer.sql";
	
	private TestDataSqlScriptGenerator() {
	    
	}
	
	public static void main(String[] args)
	{
		LOGGER.debug( String.format( "Received args: %s", Arrays.toString(args) ) );
		if (args.length != 3)
		{
			LOGGER.error("3 input parameters are required: " + Arrays.toString(args));
		}
		int featuresCount = Integer.parseInt(args[FEATURE_COUNT_INDEX]);
		int sensorCount = Integer.parseInt(args[SENSOR_COUNT_INDEX]);
		int observationsPerFeaturePerSensorCount = Integer.parseInt(args[OBSERVATION_COUNT_INDEX]);
		// try to open file
		String fileName = DEFAULT_FILENAME;
		if (args.length >= 4)
		{
			fileName = args[FILENAME_INDEX];
		}
		BufferedWriter bw = null;
		try {
			File file = new File(fileName);
			if ( fileExistsThanDeleteItAndCreateNew(file))
			{
				FileWriter fw = new FileWriter(file);
				LOGGER.debug("Writing results to file '{}'",file.getAbsolutePath());
				bw = new BufferedWriter(fw);
				// add header to sql file
				bw.write(createHeaderStatements());
				bw.flush();
				// add content to sql file
				// add features
				bw.newLine();
				bw.newLine();
				bw.write("---- START GENERATED CONTENT");
				bw.newLine();
				bw.newLine();
				bw.write("---- FEATURES");
				bw.newLine();
				Map<Double,Double> uniqueCoordinates = new HashMap<Double, Double>(featuresCount);
				for (int featureId = 0; featureId < featuresCount; featureId++)
				{
					bw.write(createFeatureStatement(featureId,
							generateRandomUniqueCoordinateTuple(-90.0,90.0,-180.0,180.0,uniqueCoordinates)));
					bw.newLine();
				}
				bw.flush();
				DateTime start = new DateTime(0l);
				bw.write("---- SENSORS incl. sensor, offering, and observation constellation");
				bw.newLine();
				for (int sensorId = 0; sensorId < sensorCount; sensorId++)
				{
					// add sensor
					bw.write(createSensorStatement(sensorId,
							generateRandomCoordinateTuple(-90.0,90.0,-180.0,180.0),
							start,
							OBSERVATION_TYPE,
							OBSERVED_PROPERTY_ID,
							FEATURE_TYPE));
					bw.newLine();
					
					// add offering
					bw.write(createOfferingStatement(sensorId));
					bw.newLine();
					
					// add observation constellation
					bw.write(createObservationConstellationStatement(OBSERVATION_TYPE,
							sensorId,
							OBSERVED_PROPERTY_ID));
					bw.newLine();
				}
				bw.flush();
				// add observations
				// observation loop
				bw.write("---- OBSERVATIONS");
				bw.newLine();
				for (int observationBatch = 0; observationBatch < observationsPerFeaturePerSensorCount; observationBatch++)
				{
					// 1 generate timestamp by incrementing from 0l by TIMESTAMP_INCREMENT_IN_MS
					DateTime timeStamp = new DateTime(observationBatch * TIMESTAMP_INCREMENT_IN_MS);
					
					// feature loop
					for (int featureId = 0; featureId < featuresCount; featureId++)
					{
						
						// sensor loop
						for (int sensorId = 0; sensorId < sensorCount; sensorId++)
						{
							// generate random result from range RESULT_MIN_VALUE and RESULT_MAX_VALUE
							bw.write(createInsertNumericObservationStatement(OBSERVATION_TYPE,
									sensorId,
									sensorId,
									OBSERVED_PROPERTY_ID,
									featureId,
									UNIT_ID,
									timeStamp,
									generateRandomResult(RESULT_MIN_VALUE,RESULT_MAX_VALUE)));
							bw.newLine();
						}
						bw.flush();
					}
				}
				bw.newLine();
				bw.newLine();
				bw.write("---- END GENERATED CONTENT");
				bw.newLine();
				bw.newLine();
				bw.write(createFooterStatements());
				bw.flush();
			}
			else 
			{
				LOGGER.error(String.format("Result file could be accessed. File: \"%s\".", file.getAbsolutePath()));
			}
		} 
		catch (IOException e) 
		{
			LOGGER.error(String.format("Exception thrown: %s",
						e.getMessage()),
					e);
		} 
		finally
		{
			if (bw != null)
			{
				try {
					bw.close();
				} catch (IOException e) {
					LOGGER.error(String.format("Exception thrown while closing stream: %s",
								e.getMessage()),
							e);
				}
			}
		}
	}

	private static String createHeaderStatements() throws FileNotFoundException
	{
		String headerStatement = new Scanner( TestDataSqlScriptGenerator.class.getResourceAsStream(HEADER_FILE_NAME) ).useDelimiter("\\A").next();
		LOGGER.debug(headerStatement);
		return headerStatement;
	}
	
	private static String createFooterStatements() throws FileNotFoundException
	{
		String headerStatement = new Scanner( TestDataSqlScriptGenerator.class.getResourceAsStream(FOOTER_FILE_NAME) ).useDelimiter("\\A").next();
		LOGGER.debug(headerStatement);
		return headerStatement;
	}

	private static boolean fileExistsThanDeleteItAndCreateNew(File file) throws IOException
	{
		return ( file.exists() && file.delete() && file.createNewFile()) || file.createNewFile();
	}

	/*
	 *  SELECT insert_numeric_observation(insert_observation(get_observation_constellation('Measurement', 'http://www.example.org/sensors/101', 
	 *  'test_offering_1', 'test_observable_property_1'), 'test_feature_1', 'test_unit_1', '2012-11-19 13:09'), 2.1);
	 */
	private static String createInsertNumericObservationStatement(
			String observationType,
			int sensorId,
			int offeringId,
			String observedProperty,
			int featureId,
			String unitId,
			DateTime timestamp,
			double value)
	{
		String insertNumericObservationStatement = String.format(SQL_INSERT_NUMERIC_OBSERVATION,
				observationType,
				Integer.toString(sensorId),
				Integer.toString(offeringId),
				observedProperty,
				Integer.toString(featureId),
				unitId,
				timestamp.toString(SQL_TIMESTAMP_PATTERN),
				Double.toString(value).replaceAll(",", "."));
		LOGGER.debug(insertNumericObservationStatement);
		return insertNumericObservationStatement;
	}

	// SELECT insert_observation_constellation('Measurement', 'http://www.example.org/sensors/101', 'test_offering_1', 'test_observable_property_1');
	private static String createObservationConstellationStatement(String observationType,
			int sensorId,
			String observedPropertyId)
	{
		String observationConstellationStatement = String.format(SQL_INSERT_OBSERVATION_CONSTELLATION,
				observationType,
				Integer.toString(sensorId),
				Integer.toString(sensorId),
				observedPropertyId);
		LOGGER.debug(observationConstellationStatement);
		return observationConstellationStatement;
	}

	// SELECT insert_offering('test_offering_1');
	private static String createOfferingStatement(int sensorId)
	{
		String offeringStatement = String.format(SQL_INSERT_OFFERING, Integer.toString(sensorId));
		LOGGER.debug(offeringStatement);
		return offeringStatement;
	}

	private static String createFeatureStatement(int featureId, Double[] coordinates)
	{
		// SELECT insert_feature_of_interest('test_feature_1', 20.401108, 49.594538);
		String featureStatement = String.format(SQL_INSERT_FEATURE,
				Integer.toString(featureId),
				Double.toString(coordinates[X_COORD_INDEX]).replaceAll(",", "."),
				Double.toString(coordinates[Y_COORD_INDEX]).replaceAll(",", "."));
		LOGGER.debug(featureStatement);
		return featureStatement;
	}

	private static String createSensorStatement(int sensorId,
			Double[] generateRandomCoordinateTuple,
			DateTime timestamp,
			String observationType,
			String observedProperty,
			String featureType)
	{
		/*
		 * SELECT insert_procedure('http://www.example.org/sensors/101', '2012-11-19 13:00', 
		 * 'test_observable_property_1', 20.401108, 49.594538, 0.0, 'Measurement', 'Point');
		 */
		String sensorStatement = String.format(SQL_INSERT_SENSOR,
				Integer.toString(sensorId),
				timestamp.toString(SQL_TIMESTAMP_PATTERN),
				observedProperty,
				Double.toString(generateRandomCoordinateTuple[X_COORD_INDEX]).replaceAll(",", "."),
				Double.toString(generateRandomCoordinateTuple[Y_COORD_INDEX]).replaceAll(",", "."),
				observationType,
				featureType);
		LOGGER.debug(sensorStatement);
		return sensorStatement;
	}

	private static Double[] generateRandomCoordinateTuple(double yMin,
			double yMax,
			double xMin,
			double xMax)
	{
		if (xMax > xMin && yMax > yMin)
		{
			Random randomizer = new Random(System.currentTimeMillis());
			double xCoord = xMin + (randomizer.nextDouble() * (Math.abs(xMax) + Math.abs(xMin)));
			double yCoord = yMin + (randomizer.nextDouble() * (Math.abs(yMax) + Math.abs(yMin)));
			Double[] result = new Double[2];
			result[X_COORD_INDEX] = xCoord;
			result[Y_COORD_INDEX] = yCoord;
			return result;
		}
		throw new IllegalArgumentException(String.format("Given parameter values wrong: xMax: %s, xMin: %s, yMax: %s, yMin: %s",
				xMax,xMin,yMax,yMin));
	}
	
	private static Double[] generateRandomUniqueCoordinateTuple(double yMin,
			double yMax,
			double xMin,
			double xMax, Map<Double, Double> uniqueCoordinates)
	{
		if (xMax > xMin && yMax > yMin && uniqueCoordinates != null)
		{
			// generate coordinates
			Double[] sample = null;
			do
			{
				sample = generateRandomCoordinateTuple(yMin, yMax, xMin, xMax);
			}
			while ( sample == null || sampleAlreadyInList(uniqueCoordinates, sample));
			
			uniqueCoordinates.put(sample[X_COORD_INDEX], sample[Y_COORD_INDEX]);
			return sample;
		}
		throw new IllegalArgumentException(String.format("Given parameter values wrong: xMax: %s, xMin: %s, yMax: %s, yMin: %s",
				xMax,xMin,yMax,yMin));
	}

	private static boolean sampleAlreadyInList(Map<Double, Double> uniqueCoordinates,
			Double[] sample)
	{
		return sample != null &&
				!uniqueCoordinates.isEmpty() && 
				uniqueCoordinates.containsKey(sample[X_COORD_INDEX]) &&
				uniqueCoordinates.get(sample[X_COORD_INDEX]).equals(sample[Y_COORD_INDEX]);
	}

	private static double generateRandomResult(double min, double max)
	{
		if (max > min)
		{
			Random randomizer = new Random(System.currentTimeMillis());
			return min + (randomizer.nextDouble() * (Math.abs(max) + Math.abs(min)));
		}
		throw new IllegalArgumentException(String.format("Give parameter values wrong: max: %s, min:%s",max,min));
	}

}
