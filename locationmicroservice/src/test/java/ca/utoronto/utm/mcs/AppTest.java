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
    final static String API_URL = "http://localhost:8000";

    private static HttpResponse<String> sendRequest(String endpoint, String method, String reqBody) throws  InterruptedException, IOException {

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL + endpoint))
                .method(method, HttpRequest.BodyPublishers.ofString(reqBody))
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }
    @Test
    public void exampleTest() {
        assertTrue(true);
    }
    @Test
    @Order(1)
    public void getNearbyDriverPass() throws JSONException, IOException, InterruptedException{

        JSONObject confirmReq = new JSONObject()
                .put("uid", "UID1")
                .put("is_driver", true);
        HttpResponse<String>   confirmRes =  sendRequest("/location/user", "PUT", confirmReq.toString());
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
        confirmRes  = sendRequest("/location/nearbyDriver/UID2?radius=300", "GET", "");
        assertEquals(HttpURLConnection.HTTP_OK, confirmRes.statusCode());

    }

    @Test
    @Order(2)
    public void getNearbyDriverFail() throws JSONException, IOException, InterruptedException{
        JSONObject confirmReq = new JSONObject()
                .put("uid", "UID1")
                .put("is_driver", true);
        HttpResponse<String>   confirmRes =  sendRequest("/location/user", "PUT", confirmReq.toString());
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
                .put("longitude", 79.3832)
                .put("latitude", 44.6532)
                .put("street", "Zingaro Zone");
        sendRequest("/location/UID2", "PATCH", confirmReq.toString());
        confirmRes  = sendRequest("/location/nearbyDriver/UID2?radius=rjf", "GET", "");
        assertEquals(HttpURLConnection.HTTP_BAD_REQUEST, confirmRes.statusCode());
    }
    @Test
    @Order(3)
    public void getNavigationPass() throws JSONException, IOException, InterruptedException{
        JSONObject confirmReq = new JSONObject()
                .put("uid", "UID1")
                .put("is_driver", true);
        HttpResponse<String>   confirmRes =  sendRequest("/location/user", "PUT", confirmReq.toString());
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
                .put("longitude", 79.3832)
                .put("latitude", 44.6532)
                .put("street", "Zingaro Zone");
        sendRequest("/location/UID2", "PATCH", confirmReq.toString());
        confirmRes  = sendRequest("/location/navigation/UID1?passengerUid=UID2", "GET", "");
        assertEquals(HttpURLConnection.HTTP_OK, confirmRes.statusCode());
    }

    @Test
    @Order(4)
    public void getNavigationFail() throws JSONException, IOException, InterruptedException{
        JSONObject confirmReq = new JSONObject()
                .put("uid", "UID1")
                .put("is_driver", true);
        HttpResponse<String>   confirmRes =  sendRequest("/location/user", "PUT", confirmReq.toString());
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
                .put("longitude", 79.3832)
                .put("latitude", 44.6532)
                .put("street", "Zingaro Zone");
        sendRequest("/location/UID2", "PATCH", confirmReq.toString());
        confirmRes  = sendRequest("/location/navigation/UID2?passengerUid=UID1", "GET", "");
        assertEquals(HttpURLConnection.HTTP_NOT_FOUND, confirmRes.statusCode());
    }


}
