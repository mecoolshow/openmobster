/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.test.sync;

import java.util.Vector;

import org.openmobster.core.mobileCloud.rimos.configuration.Configuration;
import org.openmobster.core.mobileCloud.rimos.module.connection.NetSession;
import org.openmobster.core.mobileCloud.rimos.module.connection.NetworkConnector;
import org.openmobster.core.mobileCloud.rimos.module.mobileObject.MobileObject;
import org.openmobster.core.mobileCloud.rimos.module.mobileObject.MobileObjectDatabase;
import org.openmobster.core.mobileCloud.rimos.module.sync.Alert;
import org.openmobster.core.mobileCloud.rimos.module.sync.Session;
import org.openmobster.core.mobileCloud.rimos.module.sync.Status;
import org.openmobster.core.mobileCloud.rimos.module.sync.SyncAdapter;
import org.openmobster.core.mobileCloud.rimos.module.sync.SyncException;
import org.openmobster.core.mobileCloud.rimos.module.sync.SyncMessage;
import org.openmobster.core.mobileCloud.rimos.module.sync.SyncService;
import org.openmobster.core.mobileCloud.rimos.module.sync.engine.SyncDataSource;

import test.openmobster.core.mobileCloud.rimos.testsuite.Test;


/**
 * 
 * @author openmobster@gmail.com
 *
 */
public abstract class AbstractSyncTest extends Test 
{
	protected String service;
	
	public void setUp()
	{
		this.service = (String)this.getTestSuite().getContext().getAttribute("service");
	}
	
	public void tearDown() 
	{				
		//Cleanup any sync related persisted data
		SyncDataSource.getInstance().clearAll();		
		
		this.resetServerAdapter("setUp="+this.getClass().getName()+"/CleanUp\n");
	}	
		
	protected void setUp(String operation)
	{
		try
		{
			String service = (String)this.getTestSuite().getContext().getAttribute("service");			
			MobileObjectDatabase.getInstance().deleteAll(service);
			SyncDataSource.getInstance().clearAll();
			
			if(operation.equalsIgnoreCase("add"))
			{
				//Adding records 'unique-3' and 'unique-4' to the device in addition
				//to the existing 'unique-1' and 'unique-2'			
				for(int i=3; i<5; i++)
				{
					String uniqueId = "unique-"+String.valueOf(i);
					MobileObject cour = new MobileObject();
					cour.setRecordId(uniqueId);
					cour.setStorageId(service);
					cour.setValue("from", uniqueId+"@from.com");
					cour.setValue("to", uniqueId+"@to.com");
					cour.setValue("subject", uniqueId+"/Subject");
					cour.setValue("message", "<tag apos='apos' quote=\"quote\" ampersand='&'>"+uniqueId+"/Message</tag>");
					MobileObjectDatabase.getInstance().create(cour);
				
					SyncService.getInstance().updateChangeLog(service, SyncService.OPERATION_ADD, cour.getRecordId());					
				}														
			}
			else if(operation.equalsIgnoreCase("replace"))
			{		
				//Only modifying record 'unique-2' on the device						
				for(int i=1; i<3; i++)
				{
					String uniqueId = "unique-"+String.valueOf(i);
					MobileObject cour = new MobileObject();
					cour.setRecordId(uniqueId);
					cour.setStorageId(service);
					cour.setValue("from", uniqueId+"@from.com");
					cour.setValue("to", uniqueId+"@to.com");
					cour.setValue("subject", uniqueId+"/Subject");				
					if(i == 2)
					{
						cour.setValue("message", "<tag apos='apos' quote=\"quote\" ampersand='&'>"+uniqueId+"/Updated/Client</tag>");
						MobileObjectDatabase.getInstance().create(cour);																		
						SyncService.getInstance().updateChangeLog(service, SyncService.OPERATION_UPDATE, cour.getRecordId());
					}
					else
					{
						cour.setValue("message","<tag apos='apos' quote=\"quote\" ampersand='&'>"+uniqueId+"/Message</tag>");
						MobileObjectDatabase.getInstance().create(cour);
					}														
				}								
			}
			else if(operation.equalsIgnoreCase("conflict"))
			{		
				//Only modifying record 'unique-1' on the device							
				for(int i=1; i<3; i++)
				{
					String uniqueId = "unique-"+String.valueOf(i);
					MobileObject cour = new MobileObject();
					cour.setRecordId(uniqueId);
					cour.setStorageId(service);
					cour.setValue("from", uniqueId+"@from.com");
					cour.setValue("to", uniqueId+"@to.com");
					cour.setValue("subject", uniqueId+"/Subject");				
					if(i == 1)
					{
						cour.setValue("message","<tag apos='apos' quote=\"quote\" ampersand='&'>"+uniqueId+"/Updated/Client</tag>");
						MobileObjectDatabase.getInstance().create(cour);												
						SyncService.getInstance().updateChangeLog(service, SyncService.OPERATION_UPDATE, cour.getRecordId());
					}
					else
					{
						cour.setValue("message", "<tag apos='apos' quote=\"quote\" ampersand='&'>"+uniqueId+"/Message</tag>");
						MobileObjectDatabase.getInstance().create(cour);
					}															
				}				
			}
			else if(operation.equalsIgnoreCase("delete"))
			{
				//Only delete 'unique-2' from the device				
				//Adding only 'unique-1' making it seem like unique-2 was deleted
				for(int i=1; i<2; i++)
				{
					String uniqueId = "unique-"+String.valueOf(i);
					MobileObject cour = new MobileObject();
					cour.setRecordId(uniqueId);
					cour.setStorageId(service);
					cour.setValue("from", uniqueId+"@from.com");
					cour.setValue("to", uniqueId+"@to.com");
					cour.setValue("subject", uniqueId+"/Subject");
					cour.setValue("message", "<tag apos='apos' quote=\"quote\" ampersand='&'>"+uniqueId+"/Message</tag>");
					MobileObjectDatabase.getInstance().create(cour);					
				}
				//Updating the ChangeLog to reflect having deleted 'unique-2' from the device				
				SyncService.getInstance().updateChangeLog(service, SyncService.OPERATION_DELETE, "unique-2");
			}
			else if(operation.equalsIgnoreCase("multirecord"))
			{		
				//Only modifying record 'unique-2' on the device		
				for(int i=1; i<3; i++)
				{
					String uniqueId = "unique-"+String.valueOf(i);
					MobileObject cour = new MobileObject();
					cour.setRecordId(uniqueId);
					cour.setStorageId(service);
					cour.setValue("recordId",uniqueId);
					cour.setValue("from","@from.com");
					cour.setValue("to","@to.com");
					cour.setValue("subject","/Subject");
					cour.setValue("message",(i==1)?"<tag apos='apos' quote=\"quote\" ampersand='&'>"+uniqueId+"/Message</tag>":"<tag apos='apos' quote=\"quote\" ampersand='&'>"+uniqueId+"/Updated/Client</tag>");
					MobileObjectDatabase.getInstance().create(cour);										
					if(i == 2)
					{												
						SyncService.getInstance().updateChangeLog(service, SyncService.OPERATION_UPDATE, cour.getRecordId());
					}
				}								
			}
		}
		catch(Exception e)
		{
			throw new RuntimeException(e.toString());
		}
		finally
		{
			//reset the server side sync adapter
			this.resetServerAdapter("setUp="+this.getClass().getName()+"/"+operation+"\n");
		}
	}
		
	protected void resetServerAdapter(String payload)
	{		
		NetSession netSession = null;
		try
		{
			boolean secure = Configuration.getInstance().isSSLActivated();
			netSession = NetworkConnector.getInstance().openSession(secure);
			
			String response = netSession.sendTwoWay("processorid=testsuite");
			
			if(response.indexOf("status=200")!=-1)
			{
				netSession.sendOneWay(payload);				
			}
		}
		catch(Exception e)
		{
			throw new RuntimeException(e.toString());
		}
		finally
		{
			if(netSession != null)
			{
				netSession.close();
			}
		}
	}				
	//-----------------------------------------------------------------------------------------------------------------------------------------------
	protected MobileObject getRecord(String recordId) throws SyncException
	{
		MobileObject cour = MobileObjectDatabase.getInstance().read(this.service, recordId);
		return cour;
	}
			
	protected void assertRecordPresence(String recordId, String context) throws SyncException
	{
		MobileObject record = MobileObjectDatabase.getInstance().read(this.service, recordId);
		this.assertTrue((record != null), context+"/"+recordId);
	}
		
	protected void assertRecordAbsence(String recordId, String context) throws SyncException
	{
		MobileObject record = MobileObjectDatabase.getInstance().read(this.service, recordId);
		this.assertTrue((record == null), context+"/"+recordId);
	}
				
	protected void assertAnchorFailure(Session activeSession) throws SyncException
	{						
		boolean alertFound = false;
		
		if(activeSession.getServerInitPackage().getMessages()!=null && 
		   !activeSession.getServerInitPackage().getMessages().isEmpty())
		{
			SyncMessage serverInitMessage = (SyncMessage)activeSession.getServerInitPackage().
			getMessages().elementAt(0);
			Vector alerts = serverInitMessage.getAlerts();
			for(int i=0,size=alerts.size();i<size;i++)
			{
				Alert alert = (Alert)alerts.elementAt(i);
				if(alert.getData().equals(SyncAdapter.ANCHOR_FAILURE))
				{
					alertFound = true;
					break;
				}
			}
		}
		
		this.assertTrue(alertFound, "/TestAnchorSupport/assertAnchorFailure/Anchor_Should_Be_OutOfSync");
	}
		
	public void assertPayload(Vector messages, String operation, String statusCode, String context)
	{
		for(int i=0,size=messages.size(); i<size; i++)
		{
			SyncMessage message = (SyncMessage)messages.elementAt(i);
			Vector statuses = message.getStatus();
			if(statuses != null)
			{
				for(int j=0,jSize=statuses.size(); j<jSize; j++)
				{
					Status status = (Status)statuses.elementAt(j);
					String cmd = status.getCmd();
					if(cmd.equals(operation) &&
					   !status.getData().equals(SyncAdapter.CHUNK_ACCEPTED) //ignore Chunk accepted status code
					)
					{
						this.assertEquals(status.getData(), statusCode, context);
					}
				}
			}
		}
	}	
}
