package de.kawumtech.ktharaspiservice.measurement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import de.kawumtech.ktha.restlib.sensor.pojo.SensorReading;
import de.kawumtech.ktharaspiservice.measurement.service.MeasurementService;

@Controller
public class MeasurementController
{

	@Autowired
	private MeasurementService measurementService;
	
	@RequestMapping(value="/read/{sensorName}", method=RequestMethod.GET)
	@ResponseBody
	public SensorReading<?> readSensor(@PathVariable final String sensorName)
	{
		return this.measurementService.readSensor(sensorName);
	}
}
