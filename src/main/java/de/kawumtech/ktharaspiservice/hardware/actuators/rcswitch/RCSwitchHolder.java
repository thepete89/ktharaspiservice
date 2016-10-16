package de.kawumtech.ktharaspiservice.hardware.actuators.rcswitch;

import java.util.HashMap;
import java.util.Map;

import com.pi4j.io.gpio.RaspiPin;

public class RCSwitchHolder
{
	private static final RCSwitchHolder INSTANCE = new RCSwitchHolder(); 
	
	private final Map<String, RCSwitch> rcSwitchMap = new HashMap<String, RCSwitch>();
	
	private RCSwitchHolder()
	{}
	
	public static RCSwitchHolder getInstance()
	{
		return RCSwitchHolder.INSTANCE;
	}
	
	public RCSwitch getRcSwitch(String pin)
	{
		if(!rcSwitchMap.containsKey(pin))
		{
			rcSwitchMap.put(pin, new RCSwitch(RaspiPin.getPinByName("GPIO " + pin)));
		}
		return rcSwitchMap.get(pin);
	}
}
