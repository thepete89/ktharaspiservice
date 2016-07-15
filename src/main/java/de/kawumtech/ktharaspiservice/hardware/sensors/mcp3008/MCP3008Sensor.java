package de.kawumtech.ktharaspiservice.hardware.sensors.mcp3008;

import java.io.IOException;

import org.slf4j.LoggerFactory;

import com.pi4j.io.spi.SpiChannel;

public class MCP3008Sensor
{
	private MCP3008GpioProvider mcp3008GpioProvider;
	
	private Integer pin;
	
	private SpiChannel spiChannel;
	
	private MCP3008SensorType sensorType;
	
	public MCP3008Sensor()
	{}
	
	public MCP3008Sensor(Integer pin, SpiChannel spiChannel, MCP3008SensorType sensorType)
	{
		this.pin = pin;
		this.spiChannel = spiChannel;
		this.sensorType = sensorType;
		this.createMcp3008GpioProviderInstance();
	}

	private void createMcp3008GpioProviderInstance()
	{
		try
		{
			this.mcp3008GpioProvider = new MCP3008GpioProvider(this.spiChannel);
		} catch (IOException e)
		{
			LoggerFactory.getLogger(this.getClass()).error(e.getMessage(), e);
		}
	}
	
	public Double readAnalogValue()
	{
		Double value = Double.NaN;
		if(this.mcp3008GpioProvider != null)
		{
			value = this.mcp3008GpioProvider.getValue(MCP3008Pin.ALL[this.pin]);
		}
		return value;
	}
	
	public Integer getPin()
	{
		return pin;
	}

	public void setPin(Integer pin)
	{
		this.pin = pin;
	}

	public SpiChannel getSpiChannel()
	{
		return spiChannel;
	}

	public void setSpiChannel(SpiChannel spiChannel)
	{
		this.spiChannel = spiChannel;
		this.createMcp3008GpioProviderInstance();
	}

	public MCP3008SensorType getSensorType()
	{
		return sensorType;
	}

	public void setSensorType(MCP3008SensorType sensorType)
	{
		this.sensorType = sensorType;
	}
	
}
