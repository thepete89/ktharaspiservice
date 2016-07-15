package de.kawumtech.ktharaspiservice.hardware.sensors.ds1820;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class DS1820Sensor
{
	private String sensorBaseDirectory;
	
	private String sensorHardwareId;
	
	public DS1820Sensor()
	{}
	
	public DS1820Sensor(String sensorBaseDirectory, String sensorHardwareId)
	{
		this.sensorBaseDirectory = sensorBaseDirectory;
		this.sensorHardwareId = sensorHardwareId;
	}
	
	private Path getSensorOutputFilePath()
	{
		return Paths.get(this.sensorBaseDirectory + File.separator + this.sensorHardwareId + File.separator + "w1_slave");
	}
	
	public Double readSensor() throws IOException
	{
		Double value = Double.NaN;
		Path sensorFile = this.getSensorOutputFilePath();
		if(Files.isReadable(sensorFile))
		{
			List<String> fileContents = Files.readAllLines(sensorFile);
			if(fileContents.get(0).matches("([0-9a-f]{2} ){9}: crc=[0-9a-f]{2} YES"))
			{
				String sensorData = fileContents.get(1).replaceAll("([0-9a-f]{2} ){9}t=", "");
				value = Double.valueOf(sensorData) / 1000;
			}
		}
		return value;
	}
	
	public String getFullSensorAddress()
	{
		return this.sensorBaseDirectory + File.separator + this.sensorHardwareId;
	}

	public String getSensorBaseDirectory()
	{
		return sensorBaseDirectory;
	}

	public void setSensorBaseDirectory(String sensorBaseDirectory)
	{
		this.sensorBaseDirectory = sensorBaseDirectory;
	}

	public String getSensorHardwareId()
	{
		return sensorHardwareId;
	}

	public void setSensorHardwareId(String sensorHardwareId)
	{
		this.sensorHardwareId = sensorHardwareId;
	}
	
}
