package de.kawumtech.ktharaspiservice.configuration;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("ktha")
public class PwmConfiguration
{
	private Map<String, Integer> pwm = new HashMap<String, Integer>();
	
	public Map<String, Integer> getPwm()
	{
		return this.pwm;
	}
}
