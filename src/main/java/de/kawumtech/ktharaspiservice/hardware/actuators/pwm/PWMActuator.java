package de.kawumtech.ktharaspiservice.hardware.actuators.pwm;

public class PWMActuator
{
	private int pin;
	
	private int maxValue;

	public int getPin()
	{
		return pin;
	}

	public void setPin(int pin)
	{
		this.pin = pin;
	}

	public int getMaxValue()
	{
		return maxValue;
	}

	public void setMaxValue(int maxValue)
	{
		this.maxValue = maxValue;
	}

}
