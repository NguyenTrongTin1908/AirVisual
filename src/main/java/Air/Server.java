package Air;

import java.awt.EventQueue;
import java.io.IOException;

import javax.json.Json;
import javax.json.JsonReader;

import okhttp3.*;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
/**
 * This is a first step in restful flow. We must authorize first and use the token for other request.
 */
public class Server {
	
	
	public static int buffsize = 512;
	public static int port = 1234;
	public static DatagramSocket socket;
	public static DatagramPacket dpreceive, dpsend;
	public static String jsonCountry;
	
	public static String tmp;
	public static String[] words;
	public static String[] data;
 
	 public static void main(String[] args) throws IOException {
		 
		 jsonCountry="";
		 data= new String[2];
		 data[0]=" ";
		 data[1]="";
					try {
						
						
						socket = new DatagramSocket(1234);
						dpreceive = new DatagramPacket(new byte[buffsize], buffsize);
						while(true) {
							socket.receive(dpreceive);
							tmp = new String(dpreceive.getData(), 0 , dpreceive.getLength());
							
							 data= getUrl(tmp);
							 getData(data);
							 
							if(tmp.equals("bye")) {
								
								socket.close();
								break;
							}
							// Uppercase, sent back to client
							
						}
						
						
						
						           
						
						
					} catch (Exception e) {
						e.printStackTrace();
					}
				
		 
		}
	 
	 
	 public static String[] getUrl(String tmp) throws IOException {
		 
		 
		 if(tmp.equals("Hello")) {
			       data[0]="https://api.airvisual.com/v2/countries?key=015a3445-bebe-4102-81bf-f4189f8845bf";
				   data[1]="COUNTRY";
			  return data;
			 }
		 
		 
		 else if(jsonCountry.indexOf(tmp)!=-1 || tmp.split(";").length==1) {
			  data[0]="https://api.airvisual.com/v2/states?country="+tmp+"&key=015a3445-bebe-4102-81bf-f4189f8845bf";
			   data[1]="STATE";
		       return data;
			  
			 }
		 
		 
		 
		  else if(tmp.split(";").length==2)
		  {     words=tmp.split(";");
			  data[0]="https://api.airvisual.com/v2/cities?state="+words[1]+"&country="+words[0]+"&key=015a3445-bebe-4102-81bf-f4189f8845bf";
			   data[1]="CITY";
		       return data;
		
				 
		  }
		  
		 else if(tmp.split(";").length==3) 
		 {
			 
		  data[0]=tmp;
			   data[1]="AIR_CONDITION";
		  return data;

			
			
			 
		 }
		 
			 
		 else
		 
		 return data;
	 
	 }
	 
public static void getData(String []data) throws IOException {
	
	String coor = "", weather ="", pollution="",current="";
	if(data[0].equals(" ")) {
		String error="du lieu khong hop le";
		dpsend = new DatagramPacket(error.getBytes(), error.getBytes().length, 
	   			dpreceive.getAddress(), dpreceive.getPort());
	   			
	   			socket.send(dpsend);
		
	}
	else if(!data[0].equals(tmp)) {
	 OkHttpClient client = new OkHttpClient().newBuilder()
			  .build();
			Request request = new Request.Builder()
			  .url(data[0])
			  .method("GET", null)
			  .build();
			Response response = client.newCall(request).execute();
			 String result = response.body().string();
			 
//			 System.out.println(result);
			 
			
	       JsonObject jsonObject =JsonParser.parseString(result).getAsJsonObject();
	       
	     
	      
	       jsonCountry = jsonObject.get("data").toString();
	       
	       jsonCountry = jsonCountry.replace("[","");
	       jsonCountry = jsonCountry.replace("]","");
	       jsonCountry = jsonCountry.replace("{","");
	       jsonCountry = jsonCountry.replace("}","");
	       jsonCountry = jsonCountry.replace("\"","");
	       jsonCountry = jsonCountry.replace("state:","");  
	       jsonCountry = jsonCountry.replace("city","");
	       jsonCountry = jsonCountry.replace("country","");
	       jsonCountry = jsonCountry.replace(",",", ");
	       jsonCountry = jsonCountry.replace(":","");
	       jsonCountry=data[1]+" : "+jsonCountry;


	       
	       
	       
	       if( jsonCountry.indexOf("not_found")!=-1) {
	    	   jsonCountry ="du lieu khong hop le";
			 }
	       
	       dpsend = new DatagramPacket(jsonCountry.getBytes(), jsonCountry.getBytes().length, 
	   			dpreceive.getAddress(), dpreceive.getPort());
	   			
	   			socket.send(dpsend);
	       
	 }
	 else {
		
		words=tmp.split(";");
		data[0]= "https://api.airvisual.com/v2/city?city="+words[2]+"&state="+words[1]+"&country="+words[0]+"&key=015a3445-bebe-4102-81bf-f4189f8845bf";
		 OkHttpClient client = new OkHttpClient().newBuilder()
				  .build();
				Request request = new Request.Builder()
				  .url(data[0])
				  .method("GET", null)
				  .build();
				Response response = client.newCall(request).execute();
				 String result = response.body().string();
				 
				 JsonObject jsonObject =JsonParser.parseString(result).getAsJsonObject();
				 String data_send=jsonObject.get("data").toString();
				 
//				 if(data_send.indexOf("location")!=-1)
//				 {
//					 data_send = data_send.replace("location","location\n\t");
//				 }
				 if(data_send.indexOf("coordinates")!=-1)
				 {
//					 data_send = data_send.replace("coordinates","\t"+" coordinates\n\t\t"); 
					  coor = data_send.substring(data_send.indexOf("coordinates"), data_send.indexOf("current")); 
					  coor = coor.replace(",",","+"\t\t"+"    ");
//					 System.out.print(coor);
				 }
				 if(data_send.indexOf("current")!=-1)
				 {
//					 data_send = data_send.replace("current","current" +"\n\t");
					 current = data_send.substring(data_send.indexOf("current"), data_send.indexOf("weather")); 
//					 System.out.print(coor);
				 }
				 if(data_send.indexOf("weather")!=-1)
				 {
//					 data_send = data_send.replace("weather","weather\n\t");
					  weather = data_send.substring(data_send.indexOf("weather"),data_send.indexOf("pollution")); 
					  weather = weather.replace(",",","+"\t\t");

//					 System.out.print(weather);
				 }
				 if(data_send.indexOf("pollution")!=-1)
				 {
//					 data_send = data_send.replace("pollution","\t"+"pollution\n\t");
					 pollution = data_send.substring(data_send.indexOf("pollution")); 
					  pollution = pollution.replace(",",","+"\t\t"+"  ");

//					 System.out.print(pollution);
				 }
				 String tmp = data_send.substring(data_send.indexOf("current"));
				 
				 data_send = data_send.substring(0, data_send.indexOf("coordinates")); 
				 
				 data_send = data_send+"\t"+coor;
				 data_send = data_send+"\n"+current;
				 data_send = data_send+"\n\t"+weather;
				 data_send = data_send+"\n\t"+pollution;

				 

				 
//				 data_send = data_send+tmp;

				   data_send = data_send.replace("[","");
			       data_send = data_send.replace("]","");
			       data_send = data_send.replace("{","");
			       data_send = data_send.replace("}","");
			       data_send = data_send.replace("\"","");
			      
			       data_send = data_send.replace(",","\n");
			       
				 if(data_send.indexOf("not_found")!=-1) {
					 data_send ="du lieu khong hop le";
				 }
				 
				 
				 
				 dpsend = new DatagramPacket(data_send.getBytes(), data_send.getBytes().length, 
				   			dpreceive.getAddress(), dpreceive.getPort());
				   			
				   			socket.send(dpsend);
				 
				
		      
		 
	 }
	      
	       
	      
			
	            
	 
		
	 }
}
