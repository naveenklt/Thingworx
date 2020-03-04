package com.axtel.SJO;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import java.util.Scanner;

import org.apache.log4j.Logger;
import java.lang.reflect.InvocationTargetException;

import java.rmi.RemoteException;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;

import wt.vc.config.*;
import wt.fc.*;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.part.WTPart;
import wt.pom.PersistenceException;
import wt.query.QueryException;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.vc.wip.NonLatestCheckoutException;
import wt.vc.wip.WorkInProgressException;

public  class bulkPartUpdate implements RemoteAccess
{

	//private final static Logger LOGGER = Logger.getLogger(bulkPartUpdate.class);
	public static void main(String[] args) throws FileNotFoundException, RemoteException, InvocationTargetException {
		try
		{
			System.out.println("Inside RMI Inside bulk update");
			RemoteMethodServer rms = RemoteMethodServer.getDefault();
			rms.setUserName(args[0]);
			rms.setPassword(args[1]);
			Class[] argTypes = { String.class };
			Object[] argObject = {args[2]};
			rms.invoke("createLink", bulkPartUpdate.class.getName(), null, argTypes, argObject);
			System.out.println("Invoked RMI bulk update");
			//rms.invoke("createLink", bulkPartUpdate.class.getName(), null);
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			e.printStackTrace();
			/* if (LOGGER.isDebugEnabled())
			{
				LOGGER.debug("Invalid Syntax\n windchill com.axtel.SJO.CreateUsuageLinkMKPart <ADMIN_USER> <ADMIN_PASSWORD> <CSV_FILE>");
			} */
		}
	}
	
	public static void createLink(String fileName) throws WTException ,FileNotFoundException
	{
		System.out.println("Inside bulk update");
		FileInputStream fis=new FileInputStream(fileName);       
		Scanner sc=new Scanner(fis);    //file to be scanned  
		
		Boolean firstline=true;
		String[] intname=null;
		int lineno =0;
		while(sc.hasNextLine())  
		{  
									
			String partline =sc.nextLine();
			
			//System.out.println(partline);      
			if (firstline) {
				firstline=false;
				intname=partline.split(",");
				continue;
			}
			lineno=lineno+ 1;
			System.out.print("Update: Line no. " + lineno + " Part No: ");
			String[] partinfo=partline.split(",");
			try {
				QuerySpec qs = new QuerySpec(WTPart.class);
				qs.appendWhere(new SearchCondition(WTPart.class, WTPart.NUMBER, SearchCondition.EQUAL, partinfo[0].trim()));
				//qs.appendAnd();
				//qs.appendWhere(new SearchCondition(WTPart.class, "iterationInfo.latest", "TRUE"), new int[] { 0, 1 });
				//Iterated.IterationInfo
				LatestConfigSpec confspec=new LatestConfigSpec();
								
				QueryResult qr = PersistenceHelper.manager.find(qs);
				qr=confspec.process(qr);
				WTPart myPart = null;
				if (qr.size()==0){
					System.out.println(partinfo[0].trim() + " Part not found");
				}
				while (qr.hasMoreElements()) {
					try {
					Persistable p = (Persistable) qr.nextElement();
					myPart = (WTPart) p;
					//System.out.println("Part Number: " + myPart.getNumber());

					//System.out.println("Prt Number: " + myPart.getName() + " Version: " + myPart.getVersionInfo().getIdentifier().getValue() + " Container: "+ myPart.getContainerName());
					//System.out.println("Prt Path: " + myPart.getFolderPath());

					if (!wt.vc.wip.WorkInProgressHelper.service.isCheckoutAllowed(myPart)) {
						System.out.println(myPart.getNumber() + " Checkout not allowed");
						continue;
					}

					wt.folder.Folder checkoutFolder = wt.vc.wip.WorkInProgressHelper.service.getCheckoutFolder();
					wt.vc.wip.CheckoutLink checkout = wt.vc.wip.WorkInProgressHelper.service.checkout(myPart,checkoutFolder,null);
					Persistable my_persistable = checkout.getWorkingCopy();
					com.ptc.core.lwc.server.PersistableAdapter obj = new com.ptc.core.lwc.server.PersistableAdapter(my_persistable, null, java.util.Locale.ENGLISH, null);
					//System.out.println("Internal Nmaes: " + intname.length + " Values: " + partinfo.length);
					obj.load(intname);
					for (int i = 1; i < intname.length; i++) {
						//System.out.println(intname[i] + " : " + partinfo[i]);		
						if (!partinfo[i].trim().equals("")) {
							
							//if (!intname[i].trim().equalsIgnoreCase("number")) {
								//System.out.println("Updating attribute " + intname[i] + " : " + partinfo[i].trim().replaceAll("!", ","));							
								//Object objet= obj.get(intname[i]);
								/*if (objet!=null) {
								System.out.println("Current attribute Value: " + intname[i] + " : " + objet.toString());
							}*/
								obj.set(intname[i],partinfo[i].trim());  //.replaceAll("!", ","));							
								//System.out.println("Updated " + intname[i] + " : " + partinfo[i].trim().replaceAll("!", ","));

							//}													
						}						
					}
					obj.apply();
					//obj.validate();
					wt.fc.PersistenceHelper.manager.modify(my_persistable);
					//wt.fc.PersistenceHelper.manager.save(my_persistable);
					wt.vc.wip.WorkInProgressHelper.service.checkin(myPart, "Updated By Bulk Migration");
					System.out.println(myPart.getNumber() + " Check-in Completed Success");
					}
					catch (wt.util.WTException e) {
						if (myPart!=null) {
							wt.vc.wip.WorkInProgressHelper.service.undoCheckout(myPart);
							System.out.println(myPart.getNumber() + " Check-in Failure");
						}
					}
				}
			} catch (NonLatestCheckoutException e) {
				e.printStackTrace();
			} catch (QueryException e) {
				e.printStackTrace();
			} catch (WorkInProgressException e) {
				e.printStackTrace();
			} catch (WTPropertyVetoException e) {
				e.printStackTrace();
			} catch (PersistenceException e) {
				e.printStackTrace();
			} catch (WTException e) {				
				e.printStackTrace();
			}
		}
		sc.close();     //closes the scanner  
	}
	}