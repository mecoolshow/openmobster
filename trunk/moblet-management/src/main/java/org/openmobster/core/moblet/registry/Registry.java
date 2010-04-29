/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.moblet.registry;

import java.util.List;
import java.io.InputStream;
import java.io.IOException;

import org.apache.log4j.Logger;

import org.openmobster.core.moblet.MobletApp;

/**
 * @author openmobster@gmail.com
 */
public class Registry 
{
	private static Logger log = Logger.getLogger(Registry.class);
	
	public static final String uri = "moblet-management://registry";
	
	public Registry()
	{
		
	}
	
	public void start()
	{
		
	}
	
	public void stop()
	{
		
	}
	//--------------------------------------------------------------------------------------------------
	public void register(List<MobletApp> apps)
	{
		if(apps != null && !apps.isEmpty())
		{
			for(MobletApp app: apps)
			{
				//make sure the binary location and configuration location
				//are actually available
				if(!this.doesResourceExist(app.getBinaryLocation()))
				{
					return;
				}
				
				if(!this.doesResourceExist(app.getConfigLocation()))
				{
					return;
				}
				
				MobletApp.create(app);
				
				log.info("-----------------------------------------------------------------");
				log.info("Moblet Application ("+app.getName()+") successfully deployed.....");
				log.info("-----------------------------------------------------------------");
			}
		}
	}
	
	public InputStream getAppBinary(String appUri)
	{
		List<MobletApp> apps = MobletApp.readAll();
		if(apps != null && !apps.isEmpty())
		{
			for(MobletApp app: apps)
			{
				if(app.getUri().equals(appUri))
				{
					String binaryLocation = app.getBinaryLocation();
					if(binaryLocation.startsWith("/"))
					{
						binaryLocation = binaryLocation.substring(1);
					}
					
					return Thread.currentThread().getContextClassLoader().
					getResourceAsStream(binaryLocation);
				}
			}
		}	
		return null;
	}
	
	public InputStream getAppConfig(String appUri)
	{
		List<MobletApp> apps = MobletApp.readAll();
		if(apps != null && !apps.isEmpty())
		{
			for(MobletApp app: apps)
			{
				if(app.getUri().equals(appUri))
				{
					String confLocation = app.getConfigLocation();
					if(confLocation.startsWith("/"))
					{
						confLocation = confLocation.substring(1);
					}
					
					return Thread.currentThread().getContextClassLoader().
					getResourceAsStream(confLocation);
				}
			}
		}
		return null;
	}
	
	public MobletApp getApp(String appUri) throws Exception
	{
		List<MobletApp> apps = MobletApp.readAll();
		if(apps != null && !apps.isEmpty())
		{
			for(MobletApp app: apps)
			{
				if(app.getUri().equals(appUri))
				{
					return app;
				}
			}
		}
		return null;
	}
	
	public List<MobletApp> getAllApps()
	{		
		return MobletApp.readAll();
	}	
	//---------------------------------------------------------------------------------------------------------------
	private boolean doesResourceExist(String resourceLocation)
	{
		InputStream is = null;	
		try
		{
			if(resourceLocation.startsWith("/"))
			{
				resourceLocation = resourceLocation.substring(1);
			}
			is = Thread.currentThread().getContextClassLoader().
			getResourceAsStream(resourceLocation);
			
			if(is != null)
			{
				
				return true;
			}
			
			return false;
		}
		finally
		{
			if(is != null)
			{
				try{is.close();}catch(IOException ioe){}
			}
		}
	}
}
