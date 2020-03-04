package com.axtel.SJO;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import wt.part.*;
import wt.fc.*;

import javax.ws.rs.core.Response;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import wt.doc.*;
import wt.query.*;
import wt.util.*;

@SuppressWarnings("deprecation")
public class PartWF {

	Response s;
	String a = "\"http://www.w3.org/2001/XMLSchema-instance\"";
	
	String b = "\"http://www.w3.org/2001/XMLSchema\"";
	String data="";
	@SuppressWarnings("deprecation")
	public String Part(String number,String DocNumber,int Qty) throws WTException, ClientProtocolException, IOException {
		
		
	String DocNo=DocNumber;
	
		QuerySpec qs = new QuerySpec(WTPart.class);
				qs.appendWhere(new SearchCondition(WTPart.class, WTPart.NUMBER, SearchCondition.EQUAL, number));

				QueryResult qr = PersistenceHelper.manager.find(qs);
				wt.vc.config.LatestConfigSpec configSpec = new wt.vc.config.LatestConfigSpec();
				qr = configSpec.process(qr);
				while (qr.hasMoreElements()) {
					Persistable p = (Persistable) qr.nextElement();
					WTPart myPart = (WTPart) p;
					if(myPart.isLatestIteration())
					{
					String href = null;
					URI uri;
					URL url;
					try {
						com.ptc.netmarkets.model.NmOid tgtOid = new com.ptc.netmarkets.model.NmOid(
								wt.fc.PersistenceHelper.getObjectIdentifier(myPart));
						com.ptc.netmarkets.util.misc.NmAction infoPageAction = com.ptc.netmarkets.util.misc.NmActionServiceHelper.service
								.getAction(com.ptc.netmarkets.util.misc.NmAction.Type.OBJECT, "view");
						infoPageAction.setContextObject(tgtOid);
						infoPageAction.setIcon(null);
						href = infoPageAction.getActionUrlExternal();

						 String link = "<a href='"+href+"'>"+myPart.getNumber()+"</a>";
						
							String hString=href.replaceAll("&","&amp;");
							// String link = "<a
							// href='"+href+"'>"+myPart.getNumber()+"</a>";
							uri = new URI(hString);
						url = uri.toURL();
					} catch (java.lang.Exception e) {
						e.printStackTrace();
						throw new wt.util.WTException(e);
					}

					System.out.println("link: " + url);
				
				String c = "<SJOAssignDetails xmlns:xsi=" + a + " xmlns:xsd=" + b+ ">";

				data = c + "<Productcode>" + myPart.getNumber() + "</Productcode>" 
				+ "<SJONumber>" +DocNo+ "</SJONumber>" 
			    + "<Status>" + myPart.getLifeCycleState() + "</Status>" 
				+ "<Path>" + url + "</Path>" + 
			    "<QTY>" + Qty + "</QTY>" + "</SJOAssignDetails>";
				System.out.println(data);
				
				
				DefaultHttpClient httpClient = new DefaultHttpClient();
				HttpPost postRequest = new HttpPost(
					"http://192.168.100.114/IMMS_WEBAPI/IMMS.Engineering/api/SJOAssignAPI/SJOAssign");

				StringEntity input = new StringEntity(data);
				input.setContentType("application/xml");
				postRequest.setEntity(input);

				HttpResponse response = httpClient.execute(postRequest);
	
				System.out.println(response);
					}
			}
				return data;
			}
	
			public String Document(String number,String DocNumber) throws WTException, ClientProtocolException, IOException {
				
				
			String DocNo=DocNumber;
				QuerySpec qs1 = new QuerySpec(WTDocument.class);
				qs1.appendWhere(new SearchCondition(WTDocument.class, WTDocument.NUMBER, SearchCondition.EQUAL, number));

				QueryResult qr1 = PersistenceHelper.manager.find(qs1);
				wt.vc.config.LatestConfigSpec configSpec = new wt.vc.config.LatestConfigSpec();
				qr1 = configSpec.process(qr1);
				while (qr1.hasMoreElements()) {
					Persistable p1 = (Persistable) qr1.nextElement();
					WTDocument doc1 = (WTDocument) p1;
					String href = null;
					URI uri;
					URL url;
					try {
						com.ptc.netmarkets.model.NmOid tgtOid = new com.ptc.netmarkets.model.NmOid(
								wt.fc.PersistenceHelper.getObjectIdentifier(doc1));
						com.ptc.netmarkets.util.misc.NmAction infoPageAction = com.ptc.netmarkets.util.misc.NmActionServiceHelper.service
								.getAction(com.ptc.netmarkets.util.misc.NmAction.Type.OBJECT, "view");
						infoPageAction.setContextObject(tgtOid);
						infoPageAction.setIcon(null);
						href = infoPageAction.getActionUrlExternal();
						String hString=href.replaceAll("&","&amp;");
						// String link = "<a
						// href='"+href+"'>"+myPart.getNumber()+"</a>";
						uri = new URI(hString);
						url = uri.toURL();
						
						
					} catch (java.lang.Exception e) {
						e.printStackTrace();
						throw new wt.util.WTException(e);
					}

					System.out.println("link: " + url);
				String c = "<DJODetails xmlns:xsi=" + a + " xmlns:xsd=" + b + ">";

				data = c + "<Productcode>" +doc1.getNumber() + "</Productcode>" 
				        + "<DJONumber>" + DocNo+ "</DJONumber>" 
						+ "<Status>" + doc1.getLifeCycleState() + "</Status>" 
				        + "<Path>" + url+ "</Path>" 
						+ "</DJODetails>";
				System.out.println(data);

				DefaultHttpClient httpClient = new DefaultHttpClient();
				HttpPost postRequest = new HttpPost(
					"http://192.168.100.114/IMMS_WEBAPI/IMMS.Engineering/api/DJOAPI/DJOStatus");

				StringEntity input = new StringEntity(data);
				input.setContentType("application/xml");
				postRequest.setEntity(input);

				HttpResponse response = httpClient.execute(postRequest);
				System.out.println(response);


			}

				return data;
	}
	
	}



