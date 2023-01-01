package ca.utoronto.utm.mcs;

/** 
 * Everything you need in order to send and recieve httprequests to 
 * other microservices is given here. Do not use anything else to send 
 * and/or recieve http requests from other microservices. Any other 
 * imports are fine.
 */
import java.net.HttpURLConnection;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;

import com.sun.net.httpserver.HttpExchange;
import org.bson.types.ObjectId;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class Drivetime extends Endpoint {

    /**
     * GET /trip/driverTime/:_id
     * @param _id
     * @return 200, 400, 404, 500
     * Get time taken to get from driver to passenger on the trip with
     * the given _id. Time should be obtained from navigation endpoint
     * in location microservice.
     */

    @Override
    public void handleGet(HttpExchange r) throws IOException, JSONException {
        // TODO
        String[] params = r.getRequestURI().toString().split("/");
        if (params.length != 4 || params[3].isEmpty()) {
            this.sendStatus(r, 400);

            return;
        }
        try {
            System.out.println("fssae"+params);
            String tripId = String.valueOf(params[3]);
            System.out.println("fssae : " + tripId);
            try {
                ObjectId o = new ObjectId(tripId);
            } catch (Exception e) {
                e.printStackTrace();
                this.sendStatus(r,400);
                return;
            }
            JSONObject tripinfo = this.dao.getTripFromId(tripId);
            System.out.println(tripinfo);
            if (!tripinfo.keys().hasNext()){
                this.sendStatus(r,404);
                return;
            }
            String passID = tripinfo.getString("passenger");

            String drivID = tripinfo.getString("driver");

            HttpClient client = HttpClient.newBuilder().build();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://locationmicroservice:8000/location/navigation/" + drivID + "?passengerUid=" + passID))
                    .method("GET", HttpRequest.BodyPublishers.ofString(""))
                    .build();
            HttpResponse response = null;

            try {
                response = client.send(request, HttpResponse.BodyHandlers.ofString());
            } catch (InterruptedException e) {
                e.printStackTrace();
                this.sendStatus(r, 500);
                return;
            }

            JSONObject resp = new JSONObject(response.body().toString());

            System.out.println(response.body().toString());

            if(response.statusCode()!= HttpURLConnection.HTTP_OK){
                this.sendStatus(r, response.statusCode());
                return;
            }


            if(response==null){
                this.sendStatus(r, 500);
                return;
            }

            JSONObject data = resp.getJSONObject("data");
            Integer totTime = data.getInt("total_time");
            JSONObject answer = new JSONObject();
            answer.put("arrival_time",totTime);
            JSONObject fi = new JSONObject();
            fi.put("data",answer);
            this.sendResponse(r,fi,200);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
