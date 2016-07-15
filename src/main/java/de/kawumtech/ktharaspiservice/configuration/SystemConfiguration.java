package de.kawumtech.ktharaspiservice.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("ktha.system")
public class SystemConfiguration
{
	private String homeServerEndpoint = "";
	
	private Integer timeoutBetweenReads = 0;
	
	public String getHomeServerEndpoint()
	{
		return this.homeServerEndpoint;
	}
	
	public Integer getTimeoutBetweenReads()
	{
		return this.timeoutBetweenReads;
	}
	
	public void setHomeServerEndpoint(String homeServerEndpoint)
	{
		this.homeServerEndpoint = homeServerEndpoint;
	}
	
	public void setTimeoutBetweenReads(Integer timeoutBetweenReads)
	{
		this.timeoutBetweenReads = timeoutBetweenReads;
	}
}
