package ca.utoronto.utm.mcs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Please write your tests in this class. 
 */

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AppTest {

    final static String API_URL = "http://localhost:8004";

    private static HttpResponse<String> sendRequest(String endpoint, String method, String reqBody) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL + endpoint))
                .method(method, HttpRequest.BodyPublishers.ofString(reqBody))
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    @Test
    @Order(0)
    public void setup() throws JSONException, IOException, InterruptedException{
        JSONObject confirmReq = new JSONObject()
                .put("uid", "UID1")
                .put("is_driver", true);
        HttpResponse<String> confirmRes;
         confirmRes =  sendRequest("/location/user", "PUT", confirmReq.toString());
//
        confirmReq = new JSONObject()
                .put("uid", "UID2")
                .put("is_driver", false);
        confirmRes = sendRequest("/location/user", "PUT", confirmReq.toString());
        confirmReq = new JSONObject()
                .put("longitude", 80.3832)
                .put("latitude", 43.6532)
                .put("street", "Lisa Lane");
        confirmRes = sendRequest("/location/UID1", "PATCH", confirmReq.toString());
        confirmReq = new JSONObject()
                .put("longitude", 80.3832)
                .put("latitude", 43.653)
                .put("street", "Zingaro Zone");
        sendRequest("/location/UID2", "PATCH", confirmReq.toString());
        assertEquals(HttpURLConnection.HTTP_OK, confirmRes.statusCode());
    }
    @Test
    @Order(1)
    public void tripRequestFail() throws JSONException, IOException, InterruptedException{
        JSONObject confirmReq = new JSONObject()
                .put("uid", "4532")
                .put("radius", -1);

        HttpResponse<String> confirmRes = sendRequest("/trip/request", "POST", confirmReq.toString());
        assertEquals(HttpURLConnection.HTTP_BAD_REQUEST, confirmRes.statusCode());
    }

    @Test
    @Order(2)
    public void tripConfirmFail() throws JSONException, IOException, InterruptedException{
        JSONObject confirmReq = new JSONObject()
                .put("driver", "4532")
                .put("passenger", "25345")
                .put("startTime", "wrong format");

        HttpResponse<String> confirmRes = sendRequest("/trip/confirm", "POST", confirmReq.toString());
        assertEquals(HttpURLConnection.HTTP_BAD_REQUEST, confirmRes.statusCode());
    }

    @Test
    @Order(3)
    public void patchTripFail() throws JSONException, IOException, InterruptedException{
        JSONObject confirmReq = new JSONObject()
                .put("distance", 72)
                .put("endTime", 123124)
                .put("timeElasped", 1324134)
                .put("discount", 0)
                .put("totalCost", -5)
                .put("driverPayout", -1);

        HttpResponse<String> confirmRes = sendRequest("/trip/UID2", "PATCH", confirmReq.toString());
        assertEquals(HttpURLConnection.HTTP_BAD_REQUEST, confirmRes.statusCode());
    }

    @Test
    @Order(4)
    public void tripsForPassengerFail() throws JSONException, IOException, InterruptedException{


        HttpResponse<String> confirmRes = sendRequest("/trip/passenger/", "GET", "");
        assertEquals(HttpURLConnection.HTTP_BAD_REQUEST, confirmRes.statusCode());
    }
    @Test
    @Order(5)
    public void tripsForDriverFail() throws JSONException, IOException, InterruptedException{


        HttpResponse<String> confirmRes = sendRequest("/trip/driver/", "GET", "");
        assertEquals(HttpURLConnection.HTTP_BAD_REQUEST, confirmRes.statusCode());
    }


    @Test
    @Order(6)
    public void driverTimeFail() throws JSONException, IOException, InterruptedException{


        HttpResponse<String> confirmRes = sendRequest("/trip/driverTime/lak3", "GET", "");
        assertEquals(HttpURLConnection.HTTP_BAD_REQUEST, confirmRes.statusCode());
    }


    @Test
    @Order(7)
    public void tripRequestPass() throws JSONException, IOException, InterruptedException{
        JSONObject confirmReq = new JSONObject()
                .put("uid", "UID2")
                .put("radius", 3000);

        HttpResponse<String> confirmRes = sendRequest("/trip/request", "POST", confirmReq.toString());
        assertEquals(HttpURLConnection.HTTP_OK, confirmRes.statusCode());
    }


    @Test
    @Order(8)
    public void tripConfirmPass() throws JSONException, IOException, InterruptedException{
        JSONObject confirmReq = new JSONObject()
                .put("driver", "UID1")
                .put("passenger", "UID2")
                .put("startTime", 123234143);

        HttpResponse<String> confirmRes = sendRequest("/trip/confirm", "POST", confirmReq.toString());
        JSONObject j = new JSONObject(confirmRes.body().toString());
        if(j.has("data")){
            JSONObject m = j.getJSONObject("data");
            String uid;
            uid = m.getString("_id");

        }
        assertEquals(HttpURLConnection.HTTP_OK, confirmRes.statusCode());

    }
    String uid ;
    @Test
    @Order(9)
    public void patchTripPass() throws JSONException, IOException, InterruptedException{
            JSONObject confirmReq = new JSONObject()
                    .put("driver", "UID1")
                    .put("passenger", "UID2")
                    .put("startTime", 123234143);

            HttpResponse<String> confirmRes = sendRequest("/trip/confirm", "POST", confirmReq.toString());
            JSONObject j = new JSONObject(confirmRes.body().toString());

            if(j.has("data")){
                JSONObject m = j.getJSONObject("data");

                uid = m.getString("_id");

            }
         confirmReq = new JSONObject()
                .put("distance", 72)
                .put("endTime", 123124)
                .put("timeElapsed", 1324134)
                .put("discount", 0)
                .put("totalCost", 50)
                .put("driverPayout", 32.5);

        confirmRes = sendRequest("/trip/"+uid, "PATCH", confirmReq.toString());
        assertEquals(HttpURLConnection.HTTP_OK, confirmRes.statusCode());
    }
    @Test
    @Order(10)
    public void tripsForPassengerPass() throws JSONException, IOException, InterruptedException{


        HttpResponse<String> confirmRes = sendRequest("/trip/passenger/UID2", "GET", "");
        assertEquals(HttpURLConnection.HTTP_OK, confirmRes.statusCode());
    }
    @Test
    @Order(11)
    public void tripsForDriverPass() throws JSONException, IOException, InterruptedException{


        HttpResponse<String> confirmRes = sendRequest("/trip/driver/UID1", "GET", "");
        assertEquals(HttpURLConnection.HTTP_OK, confirmRes.statusCode());
    }
    @Test
    @Order(12)
    public void driverTimePass() throws JSONException, IOException, InterruptedException{
        JSONObject confirmReq = new JSONObject()
                .put("driver", "UID1")
                .put("passenger", "UID2")
                .put("startTime", 123234143);

        HttpResponse<String> confirmRes = sendRequest("/trip/confirm", "POST", confirmReq.toString());
        JSONObject j = new JSONObject(confirmRes.body().toString());

        if(j.has("data")){
            JSONObject m = j.getJSONObject("data");

            uid = m.getString("_id");

        }
        
        confirmRes = sendRequest("/trip/driverTime/"+uid, "GET", "");
        assertEquals(HttpURLConnection.HTTP_OK,confirmRes.statusCode());
    }

}
