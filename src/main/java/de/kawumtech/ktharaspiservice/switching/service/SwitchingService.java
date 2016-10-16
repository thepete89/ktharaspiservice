package de.kawumtech.ktharaspiservice.switching.service;

import java.util.BitSet;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.wiringpi.SoftPwm;

import de.kawumtech.ktha.restlib.api.client.RestConfiguration;
import de.kawumtech.ktha.restlib.registration.RegistrationClient;
import de.kawumtech.ktha.restlib.registration.RegistrationState;
import de.kawumtech.ktharaspiservice.configuration.ActuatorConfiguration;
import de.kawumtech.ktharaspiservice.configuration.SystemConfiguration;
import de.kawumtech.ktharaspiservice.hardware.actuators.ActuatorType;
import de.kawumtech.ktharaspiservice.hardware.actuators.gpio.GPIOActuator;
import de.kawumtech.ktharaspiservice.hardware.actuators.pwm.PWMActuator;
import de.kawumtech.ktharaspiservice.hardware.actuators.rcswitch.RCSwitch;
import de.kawumtech.ktharaspiservice.hardware.actuators.rcswitch.RCSwitchActuator;
import de.kawumtech.ktharaspiservice.hardware.actuators.rcswitch.RCSwitchHolder;

@Service
public class SwitchingService
{	
	@Autowired
	private ActuatorConfiguration actuatorConfiguration;
	
	@Autowired
	private SystemConfiguration systemConfiguration;
	
	@PostConstruct
	private void init()
	{
		GpioFactory.getInstance();
		this.showActuatorConfig();
		this.registerActuators();
	}

	private void registerActuators()
	{
		RegistrationClient registrationClient = RegistrationClient.getInstance();
		if(registrationClient.getConfiguration() == null)
		{
			RestConfiguration restConfiguration = RestConfiguration.builder().connectTimeout(2000).readTimeout(2000).serviceEndpoint(this.systemConfiguration.getHomeServerEndpoint()).build();
			registrationClient.init(restConfiguration);
		}
		this.actuatorConfiguration.getPwm().forEach((key, value) -> this.registerActuator(key, registrationClient));
		this.actuatorConfiguration.getRcswitch().forEach((key, value) -> this.registerActuator(key, registrationClient));
		this.actuatorConfiguration.getGpio().forEach((key, value) -> this.registerActuator(key, registrationClient));
	}
	
	private void registerActuator(String actuatorName, RegistrationClient registrationClient)
	{
		RegistrationState registrationState = registrationClient.registerActuator(actuatorName);
		LoggerFactory.getLogger(this.getClass()).info("Actuator " + actuatorName + " is " + registrationState.name());
	}
	
	private void showActuatorConfig()
	{
		LoggerFactory.getLogger(this.getClass()).info("Configured PWM ports:");
		for (Entry<String, PWMActuator> pwmPin : this.actuatorConfiguration.getPwm().entrySet())
		{
			LoggerFactory.getLogger(this.getClass()).info("--------------------------------------------------------------------");
			LoggerFactory.getLogger(this.getClass()).info("NAME: " + pwmPin.getKey() + " - PIN: " + pwmPin.getValue().getPin());
			LoggerFactory.getLogger(this.getClass()).info("MAXVALUE: " + pwmPin.getValue().getMaxValue());
			SoftPwm.softPwmCreate(pwmPin.getValue().getPin(), 0, pwmPin.getValue().getMaxValue());
		}
		LoggerFactory.getLogger(this.getClass()).info("Configured RCSWitch ports:");
		for (Entry<String, RCSwitchActuator> rcSwitchActuator : this.actuatorConfiguration.getRcswitch().entrySet())
		{
			LoggerFactory.getLogger(this.getClass()).info("--------------------------------------------------------------------");
			LoggerFactory.getLogger(this.getClass()).info("NAME: " + rcSwitchActuator.getKey() + " - PIN: " + rcSwitchActuator.getValue().getPin());
			LoggerFactory.getLogger(this.getClass()).info("SWITCH-GROUP: " + rcSwitchActuator.getValue().getSwitchGroup() + " - SWITCH-CODE: " + rcSwitchActuator.getValue().getSwitchCode());
		}
		LoggerFactory.getLogger(this.getClass()).info("Configured GPIO ports:");
		for (Entry<String, GPIOActuator> gpioPin : this.actuatorConfiguration.getGpio().entrySet())
		{
			LoggerFactory.getLogger(this.getClass()).info("--------------------------------------------------------------------");
			LoggerFactory.getLogger(this.getClass()).info("NAME: " + gpioPin.getKey() + " - PIN: " + gpioPin.getValue().getPin());
		}
	}
	
	public void switchOn(String name)
	{
		ActuatorType type = this.getActuatorType(name);
		switch (type)
		{
			case gpio:
				this.switchGpioOn(name);
				break;
			
			case pwm:
				this.setPwm(name, this.actuatorConfiguration.getPwm().get(name).getMaxValue());
				break;
				
			case rcswitch:
				this.switchRcswitchOn(name);
				break;
				
			default:
				break;
		}
	}
	
	public void switchOff(String name)
	{
		ActuatorType type = this.getActuatorType(name);
		switch (type)
		{
			case gpio:
				this.switchGpioOff(name);
				break;
			
			case pwm:
				this.setPwm(name, 0);
				break;
				
			case rcswitch:
				this.switchRcswitchOff(name);
				break;
				
			default:
				break;
		}
	}
	
	public void setValue(String name, Object value)
	{
		ActuatorType type = this.getActuatorType(name);
		switch (type)
		{
			case pwm:
				this.setPwm(name, Integer.parseInt((String) value));
				break;
	
			default:
				break;
		}
	}
	
	private void switchGpioOn(String name)
	{
		// TODO
	}
	
	private void switchGpioOff(String name)
	{
		// TODO
	}
	
	private void switchRcswitchOn(String name)
	{
		if(actuatorConfiguration.getRcswitch().containsKey(name))
		{
			RCSwitchActuator actuator = actuatorConfiguration.getRcswitch().get(name);
			RCSwitch rcSwitch = RCSwitchHolder.getInstance().getRcSwitch(actuator.getPin());
			BitSet switchGroupAddress = RCSwitch.getSwitchGroupAddress(actuator.getSwitchGroup());
			rcSwitch.switchOn(switchGroupAddress, actuator.getSwitchCode());
		}
	}
	
	private void switchRcswitchOff(String name)
	{
		if(actuatorConfiguration.getRcswitch().containsKey(name))
		{
			RCSwitchActuator actuator = actuatorConfiguration.getRcswitch().get(name);
			RCSwitch rcSwitch = RCSwitchHolder.getInstance().getRcSwitch(actuator.getPin());
			BitSet switchGroupAddress = RCSwitch.getSwitchGroupAddress(actuator.getSwitchGroup());
			rcSwitch.switchOff(switchGroupAddress, actuator.getSwitchCode());
		}
	}
	
	private void setPwm(String pwmPin, int value)
	{
		if(this.actuatorConfiguration.getPwm().containsKey(pwmPin))
		{
			value = value > this.actuatorConfiguration.getPwm().get(pwmPin).getMaxValue() ? this.actuatorConfiguration.getPwm().get(pwmPin).getMaxValue() : value;
			int pin = this.actuatorConfiguration.getPwm().get(pwmPin).getPin();
			SoftPwm.softPwmWrite(pin, value);
		}
	}
	
	private ActuatorType getActuatorType(String name)
	{
		ActuatorType actuatorType = ActuatorType.undefined;
		if(this.actuatorConfiguration.getPwm().containsKey(name))
		{
			actuatorType = ActuatorType.pwm;
		}
		if(this.actuatorConfiguration.getRcswitch().containsKey(name))
		{
			actuatorType = ActuatorType.rcswitch;
		}
		if(this.actuatorConfiguration.getGpio().containsKey(name))
		{
			actuatorType = ActuatorType.gpio;
		}
		return actuatorType;
	}
}
