package de.kawumtech.ktharaspiservice.configuration;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import de.kawumtech.ktharaspiservice.hardware.actuators.gpio.GPIOActuator;
import de.kawumtech.ktharaspiservice.hardware.actuators.pwm.PWMActuator;
import de.kawumtech.ktharaspiservice.hardware.actuators.rcswitch.RCSwitchActuator;

@Component
@ConfigurationProperties("ktha.actuators")
public class ActuatorConfiguration
{
	private Map<String, RCSwitchActuator> rcswitch = new HashMap<String, RCSwitchActuator>();
	private Map<String, PWMActuator> pwm = new HashMap<String, PWMActuator>();
	private Map<String, GPIOActuator> gpio = new HashMap<String, GPIOActuator>();
	
	public Map<String, PWMActuator> getPwm()
	{
		return this.pwm;
	}
	
	public Map<String, RCSwitchActuator> getRcswitch()
	{
		return this.rcswitch;
	}
	
	public Map<String, GPIOActuator> getGpio()
	{
		return this.gpio;
	}
}
