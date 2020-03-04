package com.AdvRest.PostFolder;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class test2 {

	public static void main(String[] args) throws ClassNotFoundException, SQLException, JSONException {
		getFolder();
		

	
	
}
	public static void getFolder()
	{
		
	
	List<String> name = new ArrayList<String>();
	Client client = ClientBuilder.newBuilder().register(new Authen2()).build();
	
	WebTarget target = client
			.target("http://192.168.1.95/Windchill/servlet/odata/DataAdmin/Containers('OR:wt.inf.container.OrgContainer:71685')/Folders('OR:wt.folder.Cabinet:71737')/Folders" + 
					"");
	String s = target.request().get(String.class);
	
System.out.println(s);
JSONObject data = new JSONObject(s.toString());


JSONArray values = data.getJSONArray("value");

for (int i = 0; i < values.length(); i++) {

JSONObject value = values.getJSONObject(i); 
String name1 = value.getString("Name");
name.add(name1);
}
System.out.println(name);
String Foldername="Demo3";
if(name.contains(Foldername))
{
	System.out.println(Foldername+" Already Exists...");

}
else
{
	createFolder(Foldername);
	
}
}
	
public static void createFolder(String Foldername) {
		
		Client client1 = ClientBuilder.newBuilder().register(new Authen2()).build();
		WebTarget target1 = client1.target("http://192.168.1.95/Windchill/servlet/odata/DataAdmin/Containers('OR:wt.inf.container.OrgContainer:71685')/Folders");
		String w="{\"Name\":\""+Foldername+"\",\"Description\":\"folgksdhsk\"}";
		Response s1 = target1.request().post(Entity.entity(w, MediaType.APPLICATION_JSON));
		System.out.println("Response:"+s1);
		
		getFolder();
	}
}
