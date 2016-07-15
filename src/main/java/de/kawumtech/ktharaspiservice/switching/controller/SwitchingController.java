package de.kawumtech.ktharaspiservice.switching.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import de.kawumtech.ktharaspiservice.switching.service.SwitchingService;

@RestController
public class SwitchingController
{

	@Autowired
	private SwitchingService switchingService;
	
	@RequestMapping(value="/switch/on/{switchGroup}/{switchCode}", method=RequestMethod.GET)
	public void switchOn(@PathVariable final String switchGroup, @PathVariable final int switchCode)
	{
		this.switchingService.switchOn(switchGroup, switchCode);
	}
	
	@RequestMapping(value="/switch/off/{switchGroup}/{switchCode}", method=RequestMethod.GET)
	public void switchOff(@PathVariable final String switchGroup,@PathVariable final int switchCode)
	{
		this.switchingService.switchOff(switchGroup, switchCode);
	}
	
	@RequestMapping(value="/switch/pwm/{pwmPin}/{value}")
	public void setPwm(@PathVariable final String pwmPin,@PathVariable final int value)
	{
		this.switchingService.setPwm(pwmPin, value);
	}
	
}
