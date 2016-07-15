package de.kawumtech.ktharaspiservice.configuration;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import de.kawumtech.ktharaspiservice.hardware.sensors.ds1820.DS1820Sensor;
import de.kawumtech.ktharaspiservice.hardware.sensors.mcp3008.MCP3008Sensor;

@Component
@ConfigurationProperties("ktha.sensors")
public class SensorConfiguration
{
	private Map<String, DS1820Sensor> ds1820 = new HashMap<String, DS1820Sensor>();
	
	private Map<String, MCP3008Sensor> mcp3008 = new HashMap<String, MCP3008Sensor>();
	
	// TODO add boolean/switch-type sensors (on/off)
	
	public Map<String, DS1820Sensor> getDs1820()
	{
		return this.ds1820;
	}
	
	public Map<String, MCP3008Sensor> getMcp3008()
	{
		return this.mcp3008;
	}
}
