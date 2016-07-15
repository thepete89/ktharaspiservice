# ktharaspiservice
KT Home Automation System Raspberry Pi Service - Version 0.0.3

This is the Raspberry Pi part of the KT Home Automation System. It requires wiringPi to be installed on your Raspberry, as it is
used to control the GPIO ports via pi4j. For one-wire sensor support and/or MCP3008 A/D converter support you need the corresponding
kernel modules enabled and configured:

- w1-gpio for one-wire DS1820-type sensors
- raspi-config > Advanced Options > SPI for MCP3008 A/D converter chip over hardware spi (software spi is not implemented at this point)

A Java implementation of RCSwitch is used to control 433 MHz electrical outlet switches, Pi4J's softPWM-Feature is used to use any GPIO pin
of the Pi as PWM output. This makes it possible to control RGB LED strips, for example. The PWM pins are configurable with Spring's
application.yml file, along with sensors and some general system settings.

#### WARNING: BETA SOFTWARE!
Please be aware that this system is a hobbyist's project and my first step into the field of home automation, and is primarily
developed to control a bunch of infrared heating units used in my home. It is nowhere near finished nor stable for production
and at the moment just a basic implementation to test things out. More features are comming soon, so expect rapid changes. You have
been warned!
