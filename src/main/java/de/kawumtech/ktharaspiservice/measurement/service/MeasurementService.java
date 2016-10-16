package de.kawumtech.ktharaspiservice.measurement.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import de.kawumtech.ktha.restlib.api.client.RestConfiguration;
import de.kawumtech.ktha.restlib.registration.RegistrationClient;
import de.kawumtech.ktha.restlib.registration.RegistrationState;
import de.kawumtech.ktha.restlib.sensor.client.SensorReadingClient;
import de.kawumtech.ktha.restlib.sensor.pojo.SensorReading;
import de.kawumtech.ktharaspiservice.configuration.SensorConfiguration;
import de.kawumtech.ktharaspiservice.configuration.SystemConfiguration;
import de.kawumtech.ktharaspiservice.hardware.sensors.SensorType;
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
		this.sensorConfiguration.getDs1820().forEach((key, value) -> this.registerSensor(key));
		this.sensorConfiguration.getMcp3008().forEach((key, value) -> this.registerSensor(key));
		LoggerFactory.getLogger(this.getClass()).info("All sensors registered!");
	}
	
	private void registerSensor(String sensorName)
	{
		RegistrationState state = this.registrationClient.registerSensor(sensorName);
		LoggerFactory.getLogger(this.getClass()).info("Sensor " + sensorName + " is " + state.name());
	}
	
	private void initializeRestClients()
	{
		LoggerFactory.getLogger(this.getClass()).info("Creating and initializing REST clients");
		RestConfiguration configuration = RestConfiguration.builder().connectTimeout(2000).readTimeout(2000).serviceEndpoint(this.systemConfiguration.getHomeServerEndpoint()).build();
		this.sensorReadingClient = SensorReadingClient.getInstance();
		this.sensorReadingClient.init(configuration);
		this.registrationClient = RegistrationClient.getInstance();
		this.registrationClient.init(configuration);
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
	
	public SensorReading<?> readSensor(String sensorName)
	{
		SensorReading<?> reading = new SensorReading<>();
		SensorType type = this.getSensorType(sensorName);
		switch (type)
		{
		case ds1820:
			reading = this.readValueFromDs1820(sensorName);
			break;
			
		case mcp3008:
			reading = this.readValueFromMcp3008(sensorName);
			break;

		default:
			break;
		}
		return reading;
	}

	private SensorType getSensorType(String sensorName)
	{
		SensorType type = SensorType.undefined;
		if(this.sensorConfiguration.getDs1820().containsKey(sensorName))
		{
			type = SensorType.ds1820;
		}
		if(this.sensorConfiguration.getMcp3008().containsKey(sensorName))
		{
			type = SensorType.mcp3008;
		}
		return type;
	}
	
	private SensorReading<Double> readValueFromMcp3008(String sensorName)
	{
		SensorReading<Double> reading = new SensorReading<Double>();
		reading.setSensorName(sensorName);
		reading.setValue(0.0);
		if(this.sensorConfiguration.getMcp3008().containsKey(sensorName))
		{
			MCP3008Sensor sensor = this.sensorConfiguration.getMcp3008().get(sensorName);
			Double value = sensor.readAnalogValue();
			if(sensor.getSensorType().equals(MCP3008SensorType.TEMPERATURE))
			{
				value = (value * VOLTAGE_PER_UNIT) / DEGREE_CONVERSION_FACTOR;
			}
			reading.setValue(value);
		}
		return reading;
	}

	private SensorReading<Double> readValueFromDs1820(String sensorName)
	{
		SensorReading<Double> reading = new SensorReading<Double>();
		reading.setSensorName(sensorName);
		reading.setValue(0.0);
		if(this.sensorConfiguration.getDs1820().containsKey(sensorName))
		{
			try
			{
				reading.setValue(this.sensorConfiguration.getDs1820().get(sensorName).readSensor());
			} catch (IOException e)
			{
				LoggerFactory.getLogger(this.getClass()).error("Could not read Sensor " + sensorName + " - threw IOException");
			}
		}
		return reading;
	}
	
	@Scheduled(fixedRateString="${ktha.system.timeoutBetweenReads}", initialDelay = 1000)
	private void queryAllSensors()
	{
		LoggerFactory.getLogger(this.getClass()).info("Quering Sensors..");
		@SuppressWarnings("rawtypes")
		List<SensorReading> readings = new ArrayList<SensorReading>();
		this.readDs1820Sensors(readings);
		this.read3008Sensors(readings);
		this.sensorReadingClient.sendMultipleReadings(readings);
	}

	private void read3008Sensors(@SuppressWarnings("rawtypes") List<SensorReading> readings)
	{
		for (Entry<String, MCP3008Sensor> mcp3008Sensor : this.sensorConfiguration.getMcp3008().entrySet())
		{
			readings.add(this.readValueFromMcp3008(mcp3008Sensor.getKey()));
		}
	}

	private void readDs1820Sensors(@SuppressWarnings("rawtypes") List<SensorReading> readings)
	{
		for (Entry<String, DS1820Sensor> ds1820sensor : this.sensorConfiguration.getDs1820().entrySet())
		{
			readings.add(this.readValueFromDs1820(ds1820sensor.getKey()));
		}
	}
}
