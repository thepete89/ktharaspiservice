package de.kawumtech.ktharaspiservice.switching.service;

import java.util.BitSet;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.wiringpi.SoftPwm;

import de.kawumtech.ktharaspiservice.configuration.PwmConfiguration;
import de.kawumtech.ktharaspiservice.hardware.rcswitch.RCSwitch;

@Service
public class SwitchingService
{
	// TODO change this to config value!
	private static final Pin TRANSMITTER_PIN = RaspiPin.GPIO_04;
	private RCSwitch rcSwitch;
	
	@Autowired
	private PwmConfiguration pwmConfiguration;
	
	@PostConstruct
	private void init()
	{
		this.rcSwitch = new RCSwitch(TRANSMITTER_PIN);
		LoggerFactory.getLogger(this.getClass()).info("Configured PWM ports:");
		for (Entry<String, Integer> pwmPin : this.pwmConfiguration.getPwm().entrySet())
		{
			LoggerFactory.getLogger(this.getClass()).info("NAME: " + pwmPin.getKey() + " - PIN: " + pwmPin.getValue());
			SoftPwm.softPwmCreate(pwmPin.getValue(), 0, 100);
		}
	}
	
	public void switchOn(String switchGroup, int switchCode)
	{
		BitSet switchGroupAddress = RCSwitch.getSwitchGroupAddress(switchGroup);
		this.rcSwitch.switchOn(switchGroupAddress, switchCode);
	}
	
	public void switchOff(String switchGroup, int switchCode)
	{
		BitSet switchGroupAddress = RCSwitch.getSwitchGroupAddress(switchGroup);
		this.rcSwitch.switchOff(switchGroupAddress, switchCode);
	}
	
	public void setPwm(String pwmPin, int value)
	{
		if(this.pwmConfiguration.getPwm().containsKey(pwmPin))
		{
			value = value > 100 ? 100 : value;
			int pin = this.pwmConfiguration.getPwm().get(pwmPin);
			SoftPwm.softPwmWrite(pin, value);
		}
	}
}
