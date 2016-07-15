package de.kawumtech.ktharaspiservice.measurement.service;

import java.io.IOException;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import de.kawumtech.ktha.restlib.registration.RegistrationClient;
import de.kawumtech.ktha.restlib.registration.RegistrationState;
import de.kawumtech.ktha.restlib.sensor.client.SensorReadingClient;
import de.kawumtech.ktharaspiservice.configuration.SensorConfiguration;
import de.kawumtech.ktharaspiservice.configuration.SystemConfiguration;
import de.kawumtech.ktharaspiservice.hardware.sensors.ds1820.DS1820Sensor;
import de.kawumtech.ktharaspiservice.hardware.sensors.mcp3008.MCP3008Sensor;
import de.kawumtech.ktharaspiservice.hardware.sensors.mcp3008.MCP3008SensorType;

@Service
public class MeasurementService
{	
	private static final double VOLTAGE_PER_UNIT = 0.003225806451612;
	
	private static final double DEGREE_CONVERSION_FACTOR = 0.01;
	
	@Autowired
	private SensorConfiguration sensorConfiguration;
	
	@Autowired
	private SystemConfiguration systemConfiguration;
	
	private SensorReadingClient sensorReadingClient;
	
	private RegistrationClient registrationClient;
	
	@PostConstruct
	private void init()
	{
		this.showConfig();
		this.initializeRestClients();
		this.registerSensors();
	}

	private void registerSensors()
	{
		LoggerFactory.getLogger(this.getClass()).info("Registering sensors.");
		for (Entry<String, DS1820Sensor> sensorEntry : this.sensorConfiguration.getDs1820().entrySet())
		{
			RegistrationState state = this.registrationClient.registerSensor(sensorEntry.getKey());
			LoggerFactory.getLogger(this.getClass()).info("Sensor " + sensorEntry.getKey() + " is " + state.name());
		}
		for (Entry<String, MCP3008Sensor> sensorEntry : this.sensorConfiguration.getMcp3008().entrySet())
		{
			RegistrationState state = this.registrationClient.registerSensor(sensorEntry.getKey());
			LoggerFactory.getLogger(this.getClass()).info("Sensor " + sensorEntry.getKey() + " is " + state.name());
		}
		LoggerFactory.getLogger(this.getClass()).info("All sensors registered!");
	}

	private void initializeRestClients()
	{
		LoggerFactory.getLogger(this.getClass()).info("Creating and initializing REST clients");
		this.sensorReadingClient = new SensorReadingClient();
		this.sensorReadingClient.init(this.systemConfiguration.getHomeServerEndpoint());
		this.registrationClient = new RegistrationClient();
		this.registrationClient.init(this.systemConfiguration.getHomeServerEndpoint());
	}

	private void showConfig()
	{
		LoggerFactory.getLogger(this.getClass()).info("Connected DS1820-Type Sensors:");
		for (Entry<String, DS1820Sensor> sensorEntry : this.sensorConfiguration.getDs1820().entrySet())
		{
			LoggerFactory.getLogger(this.getClass()).info("NAME: " + sensorEntry.getKey() + " - BASE: " + sensorEntry.getValue().getSensorBaseDirectory() + ", HW-ID: " + sensorEntry.getValue().getSensorHardwareId());
		}
		LoggerFactory.getLogger(this.getClass()).info("Connected MCP3008-Type Sensors:");
		for (Entry<String, MCP3008Sensor> sensorEntry : this.sensorConfiguration.getMcp3008().entrySet())
		{
			LoggerFactory.getLogger(this.getClass()).info("NAME: " + sensorEntry.getKey() + " - SPICHANNEL: " + sensorEntry.getValue().getSpiChannel().name() + ", PIN: " + sensorEntry.getValue().getPin() + ", TYPE: " + sensorEntry.getValue().getSensorType().name());
		}
		LoggerFactory.getLogger(this.getClass()).info("System Configuration: ");
		LoggerFactory.getLogger(this.getClass()).info("HomeServerEndpoint: " + this.systemConfiguration.getHomeServerEndpoint());
		LoggerFactory.getLogger(this.getClass()).info("Timeout between reads: " + this.systemConfiguration.getTimeoutBetweenReads() + "ms");
	}
	
	public double readSensor(String sensorName)
	{
		double value = 0;
		value = readValueFromDs1820(sensorName, value);
		value = readValueFromMcp3008(sensorName, value);
		return value;
	}

	private double readValueFromMcp3008(String sensorName, double value)
	{
		if(this.sensorConfiguration.getMcp3008().containsKey(sensorName))
		{
			MCP3008Sensor sensor = this.sensorConfiguration.getMcp3008().get(sensorName);
			value = sensor.readAnalogValue();
			if(sensor.getSensorType().equals(MCP3008SensorType.TEMPERATURE))
			{
				value = (value * VOLTAGE_PER_UNIT) / DEGREE_CONVERSION_FACTOR;
			}
		}
		return value;
	}

	private double readValueFromDs1820(String sensorName, double value)
	{
		if(this.sensorConfiguration.getDs1820().containsKey(sensorName))
		{
			try
			{
				value = this.sensorConfiguration.getDs1820().get(sensorName).readSensor();
			} catch (IOException e)
			{
				LoggerFactory.getLogger(this.getClass()).error("Could not read Sensor " + sensorName + " - threw IOException");
			}
		}
		return value;
	}
	
	@Scheduled(fixedRateString="${ktha.system.timeoutBetweenReads}", initialDelay = 1000)
	private void queryAllSensors()
	{
		LoggerFactory.getLogger(this.getClass()).info("Quering Sensors..");
		this.queryDs1820Sensors();
		this.queryMcp3008Sensors();
	}

	private void queryMcp3008Sensors()
	{
		for (Entry<String, MCP3008Sensor> mcp3008Sensor : this.sensorConfiguration.getMcp3008().entrySet())
		{
			Double value = this.readSensor(mcp3008Sensor.getKey());
			try
			{				
				this.sensorReadingClient.sendNumericReading(value, mcp3008Sensor.getKey());
			}
			catch(RestClientException e)
			{
				LoggerFactory.getLogger(this.getClass()).error(e.getMessage(), e);
			}
		}
	}

	private void queryDs1820Sensors()
	{
		for (Entry<String, DS1820Sensor> ds1820sensor : this.sensorConfiguration.getDs1820().entrySet())
		{
			Double value = this.readSensor(ds1820sensor.getKey());
			try
			{
				this.sensorReadingClient.sendNumericReading(value, ds1820sensor.getKey());
			} catch (RestClientException e)
			{
				LoggerFactory.getLogger(this.getClass()).error(e.getMessage(), e);
			}
		}
	}
}
