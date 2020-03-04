package com.axtel.SJO;

import wt.fc.*;
import wt.query.*;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.inf.container.*;
import wt.inf.template.*;
import wt.pdmlink.PDMLinkProduct;
public class ProductCreation {
@SuppressWarnings("deprecation")
public static void main(String args[]) throws WTException, WTContainerException,WTPropertyVetoException
{
	wt.method.RemoteMethodServer rms = wt.method.RemoteMethodServer.getDefault();
    rms.setUserName("demo");
    rms.setPassword("ptc");
	 String name = "Sale"; 
	 
	 String container_path = "/wt.inf.container.WTContainer=Demo Organization";
	String temp="wt.inf.template.DefaultWTContainerTemplate=General Product";
	 //String number = "565567";wt.inf.template.DefaultWTContainerTemplate=
	QuerySpec qs = new QuerySpec(PDMLinkProduct.class);
	SearchCondition sc = new SearchCondition(PDMLinkProduct.class, PDMLinkProduct.NAME, SearchCondition.EQUAL, name);
	qs.appendWhere(sc);
	QueryResult qr = PersistenceHelper.manager.find(qs);
	if(qr.size()==0){
	 wt.pdmlink.PDMLinkProduct doc1 = wt.pdmlink.PDMLinkProduct.newPDMLinkProduct();
	 WTContainerRef containerRef = WTContainerHelper.service.getByPath(container_path);
	 
			 WTContainerTemplateRef containerTemplateRef = ContainerTemplateHelper.service
			 .getContainerTemplateRef(containerRef, temp, PDMLinkProduct.class);
			 System.out.println( containerTemplateRef);
	 	doc1.setName(name);
	 	
		doc1.setDescription("PRODUCT CREATION");
		
		doc1.setContainerReference(containerRef);
		doc1.setContainerTemplateReference(containerTemplateRef);
		doc1=(PDMLinkProduct) WTContainerHelper.service.create(doc1);
		
		doc1 = (wt.pdmlink.PDMLinkProduct) wt.fc.PersistenceHelper.manager.store(doc1);
		
        
	}
	else
	{
		System.out.println("Already exists");
	}
	}
}
	

