package ca.utoronto.utm.mcs;

/** 
 * Everything you need in order to send and recieve httprequests to 
 * other microservices is given here. Do not use anything else to send 
 * and/or recieve http requests from other microservices. Any other 
 * imports are fine.
 */
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;

import java.net.HttpURLConnection;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;


import com.sun.net.httpserver.HttpExchange;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Iterator;



public class Request extends Endpoint {

    /**
     * POST /trip/request
     * @body uid, radius
     * @return 200, 400, 404, 500
     * Returns a list of drivers within the specified radius 
     * using location microservice. List should be obtained
     * from navigation endpoint in location microservice
     */

    @Override
    public void handlePost(HttpExchange r) throws IOException,JSONException{
        // TODO

        // check if request url isn't malformed
        String[] splitUrl = r.getRequestURI().getPath().split("/");
        if (splitUrl.length != 3) {
            this.sendStatus(r, 400);
            return;
        }

        try {
            JSONObject body = new JSONObject(Utils.convert(r.getRequestBody()));
            String fields[] = {"uid", "radius"};
            Class<?> fieldClasses[] = {String.class, Integer.class};
            if (!validateFields(body, fields, fieldClasses)) {
                this.sendStatus(r, 400);
                return;
            }

            String uid = body.getString("uid");
            int radius = body.getInt("radius");

            if(radius<=0) {
                this.sendStatus(r, 400);
                return;
            }



            HttpClient client = HttpClient.newBuilder().build();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://locationmicroservice:8000/location/nearbyDriver/" + uid + "?radius=" + Integer.toString(radius)))
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

            if(response.statusCode()!=HttpURLConnection.HTTP_OK){
                this.sendStatus(r, response.statusCode());
                return;
            }


            if(response==null){
                this.sendStatus(r, 500);
                return;
            }

            JSONObject data = resp.getJSONObject("data");
            Iterator<String> keys = data.keys();
            ArrayList<String> uids = new ArrayList<>();
            while(keys.hasNext()) {
                String key = keys.next();
                uids.add(key);
                System.out.println(key);
            }

            if(uids.isEmpty()){
                this.sendStatus(r, 404);
                return;
            }

            JSONObject answer = new JSONObject();
            answer.put("data", uids);

            this.sendResponse(r, answer, 200);


//            System.out.println(response.body().toString());

            //Use the response return a list of driver id



        }
        catch(Exception e){
            e.printStackTrace();
            this.sendStatus(r, 500);
        }


    }
}
