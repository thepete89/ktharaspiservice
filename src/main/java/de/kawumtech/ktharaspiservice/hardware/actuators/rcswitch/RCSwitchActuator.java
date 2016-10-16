package de.kawumtech.ktharaspiservice.hardware.actuators.rcswitch;

public class RCSwitchActuator
{
	private String pin;
	
	private String switchGroup;
	
	private int switchCode;
	
	public String getPin()
	{
		return pin;
	}

	public void setPin(String pin)
	{
		this.pin = pin;
	}

	public String getSwitchGroup()
	{
		return switchGroup;
	}

	public void setSwitchGroup(String switchGroup)
	{
		this.switchGroup = switchGroup;
	}

	public int getSwitchCode()
	{
		return switchCode;
	}

	public void setSwitchCode(int switchCode)
	{
		this.switchCode = switchCode;
	}
}
