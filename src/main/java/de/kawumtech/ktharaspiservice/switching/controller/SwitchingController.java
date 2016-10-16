package de.kawumtech.ktharaspiservice.switching.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.kawumtech.ktharaspiservice.switching.service.SwitchingService;

@RestController
public class SwitchingController
{

	@Autowired
	private SwitchingService switchingService;
		
	@RequestMapping(value="/switch/set/{actuator}/{value}")
	public void setValue(@PathVariable final String actuator, @PathVariable final Object value)
	{
		this.switchingService.setValue(actuator, value);
	}
	
	@RequestMapping(value="/switch/on/{actuator}")
	public void switchOn(@PathVariable final String actuator)
	{
		this.switchingService.switchOn(actuator);
	}
	
	@RequestMapping(value="/switch/off/{actuator}")
	public void switchOff(@PathVariable final String actuator)
	{
		this.switchingService.switchOff(actuator);
	}
}
