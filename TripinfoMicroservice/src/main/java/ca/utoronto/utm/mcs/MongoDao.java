package ca.utoronto.utm.mcs;


import com.mongodb.BasicDBObject;
import com.mongodb.client.*;
import io.github.cdimascio.dotenv.Dotenv;
import org.bson.Document;

import com.mongodb.BasicDBObject;



import com.mongodb.client.*;

import io.github.cdimascio.dotenv.Dotenv;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import com.mongodb.client.MongoCollection;



public class MongoDao {


	public MongoCollection<Document> collection;

	private final String username = "root";
	private final String password = "123456";
	private final String dbName = "trip";
	public MongoDao() {
		// TODO:
		// Connect to the mongodb database and create the database and collection.
		// Use Dotenv like in the DAOs of the other microservices.
		Dotenv dotenv = Dotenv.load();
		String addr = dotenv.get("MONGODB_ADDR");
		String uriDb = String.format("mongodb://%s:%s@%s:27017", username, password, addr);
		MongoClient mongoClient = MongoClients.create(uriDb);
		MongoDatabase database = mongoClient.getDatabase(this.dbName);
		this.collection = database.getCollection("trips");
	}


	// *** implement database operations here *** //
	public String confirmTrip (String driver, String passenger, int startTime) {
		Document doc = new Document();
		doc.put("driver", driver);
//		doc.put("driverPayout", 0);
		doc.put("passenger", passenger);
//		doc.put("discount",0);
		doc.put("startTime", startTime);
//		doc.put("endTime", null);
//		doc.put("timeElapsed", null);
//		doc.put("distance", 0);
//		doc.put("totalCost", 0);

		try {
			this.collection.insertOne(doc);
			return doc.get("_id").toString();
		} catch (Exception e) {
			System.out.println("Error occurred");
		}
		return null;}
	
	public ArrayList<JSONObject> getTripInfoDriver (String duid) {

		BasicDBObject whereQuery = new BasicDBObject();
		whereQuery.put("driver", duid);

		FindIterable<Document> cursor = collection.find(whereQuery);
		ArrayList<JSONObject>  j = new ArrayList<JSONObject>();
		for (Document trip: cursor
		) {
			JSONObject a = new JSONObject(trip);
			a.remove("driver");
			j.add(a);
			System.out.println(trip.toJson());
		}
		return j;

	}


	// *** implement database operations here *** //


	public ArrayList<JSONObject> getTripInfobyPid (String puid) {
  BasicDBObject whereQuery = new BasicDBObject();
  whereQuery.put("passenger", puid);
		FindIterable<Document> cursor = collection.find(whereQuery);
		ArrayList<JSONObject>  j = new ArrayList<JSONObject>();
		for (Document trip: cursor
			 ) {
			JSONObject a = new JSONObject(trip);
			a.remove("passenger");
			j.add(a);
			System.out.println(trip.toJson());
		}
		return j;
	}


	public JSONObject getTripFromId (String Tripid) throws JSONException {

		BasicDBObject whereQuery = new BasicDBObject();
		whereQuery.put("_id", new ObjectId(Tripid));
		Document cursor = collection.find(whereQuery).first();
		try {
			JSONObject a = new JSONObject(cursor.toJson());
			return a;
		}catch (Exception e){
			e.printStackTrace();

		}

		return null;
	}


	public boolean updateTrip( String _id, double distance, int endTime, String timeElapsed, double discount, double totalCost, double driverPayout){
		Document query = new Document();
		query.put("_id", new ObjectId(_id));


		Document newdoc = new Document();
		newdoc.put("distance", distance);
		newdoc.put("endTime", endTime);
		newdoc.put("timeElasped", timeElapsed);
		newdoc.put("discount", discount);
		newdoc.put("totalCost", totalCost);
		newdoc.put("driverPayout", driverPayout);

		Document update = new Document();
		update.put("$set", newdoc);

		try {
			Document d = this.collection.findOneAndUpdate(query, update);
			if(d.isEmpty()){
				return false;
			}
			return true;
		}
	 catch (Exception e) {
		System.out.println("Not Found");
	}
		return false;
	}



}