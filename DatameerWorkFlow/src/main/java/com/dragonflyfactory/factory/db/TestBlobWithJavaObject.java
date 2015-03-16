package com.dragonflyfactory.factory.db;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.sql.Blob;

import javax.sql.rowset.serial.SerialBlob;

import com.dragonflyfactory.factory.workflowsave.AllJobsInOneHashMap;

public class TestBlobWithJavaObject 
{
   static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
   static final String DB_URL = "jdbc:mysql://twitternewyork.c3lq996wo33i.us-east-1.rds.amazonaws.com:3306/use_case_3";

   static final String USER = "dragonfly";
   static final String PASS = "datafactory";
   
   public static void main(String[] args) 
   {
	   insertDatameerThingsIntoMysqlDb();
	   createDatameerThingsFromMySqlDB();
   }
   
   public static void insertDatameerThingsIntoMysqlDb() {

	   Connection conn = null;	   
	   Statement stmt = null;
	   
	   try{
		   Class.forName("com.mysql.jdbc.Driver");

		   System.out.println("Connecting to database...");
		   conn = DriverManager.getConnection(DB_URL,USER,PASS);
   
		   
		   HashMap map = AllJobsInOneHashMap.giveDatameerObjectsASMap("", "", "", "");
		   
		   HashMapSerialized hashMapSerialized=new HashMapSerialized(map);
           
           // To store the meta data of jobs of datameer as Binary Large Object into MYSQL
		   String clearSql = "delete from TestBlob ";
		   ResultSet rs = stmt.executeQuery(clearSql);
           String insertSql = "INSERT INTO TestBlob values (?)";
           PreparedStatement statement = conn.prepareStatement(insertSql);
           
//           FileSerialize fs = new FileSerialize();
//           fs.writeObject2File(map);
//           
//           InputStream inputStream = new FileInputStream(new File("DatameerMetadata.dm"));
           
           Blob blob= new SerialBlob(hashMapSerialized.getHashMapSerializedBytes());
//           
           statement.setBlob(1,blob );
           
          // statement.setBlob(1, hashMapSerialized.);

           int row = statement.executeUpdate();
           if (row > 0) {
               System.out.println("A contact was inserted with JSON file.");
           }
           
           conn.close();
           
	   }catch(SQLException se){
		   se.printStackTrace();
	   }catch(Exception e){
		   e.printStackTrace();
	   }  
   
   }
   
   public static void createDatameerThingsFromMySqlDB() {
	   

	   Connection conn = null;	   
	   Statement stmt = null;
	   
	   try{
		   Class.forName("com.mysql.jdbc.Driver");

		   System.out.println("Connecting to database...");
		   conn = DriverManager.getConnection(DB_URL,USER,PASS);
           
           // To retrieve the blob data from MYSQL
		   String sql = "select * from TestBlob";		   
           stmt = conn.createStatement();
           HashMap newMap=null;
           ResultSet rs = stmt.executeQuery(sql);
           System.out.println(rs);
           
           if(rs.getRow() == 0)
           {
        	   
        	   
        		   Blob b = rs.getBlob(1);
        		   InputStream inputStream = b.getBinaryStream();

        		   try
        		   {	
        			   ObjectInputStream out = new ObjectInputStream(inputStream);
        			   newMap = (HashMap) out.readObject();
        			   out.close();
        			   inputStream.close();

        			   System.out.println(newMap);
        		   }catch(IOException i)
        		   {
        			   i.printStackTrace();        	       
        		   }
        	   
        	   AllJobsInOneHashMap.createGivenDatameerObjectsASMapIntoDataMeerJobs(newMap, "", "", "", "");
           }
           else
           {
        	   System.out.println("No record");
           }
           conn.close();
           
	   }catch(SQLException se){
		   se.printStackTrace();
	   }catch(Exception e){
		   e.printStackTrace();
	   }  
   
   }
}