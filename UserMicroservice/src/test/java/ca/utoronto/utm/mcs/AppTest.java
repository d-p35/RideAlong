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
    @Order(1)
    public void userRegisterPass() throws JSONException, IOException, InterruptedException{
        JSONObject confirmReq = new JSONObject()
                .put("email", "david@email.com")
                .put("name", "David")
                .put("password", "1234");
        HttpResponse<String> confirmRes = sendRequest("/user/register", "POST", confirmReq.toString());
        assertEquals(HttpURLConnection.HTTP_OK, confirmRes.statusCode());
    }



    @Test
    @Order(2)
    public void userRegisterFail() throws JSONException, IOException, InterruptedException{
        JSONObject confirmReq = new JSONObject()
                .put("email", "david@email.com")
                .put("name", "differentName")
                .put("password", "differentPass");
        HttpResponse<String> confirmRes = sendRequest("/user/register", "POST", confirmReq.toString());
        assertEquals(HttpURLConnection.HTTP_CONFLICT, confirmRes.statusCode());
    }


    @Test
    @Order(3)
    public void userLoginPass() throws JSONException, IOException, InterruptedException{
        JSONObject confirmReq = new JSONObject()
                .put("email", "david@email.com")
                .put("password", "1234");
        HttpResponse<String> confirmRes = sendRequest("/user/login", "POST", confirmReq.toString());
        assertEquals(HttpURLConnection.HTTP_OK, confirmRes.statusCode());
    }

    @Test
    @Order(4)
    public void userLoginFail() throws JSONException, IOException, InterruptedException{
        JSONObject confirmReq = new JSONObject()
                .put("email", "david@email.com")
                .put("password", "wrongPassword");
        HttpResponse<String> confirmRes = sendRequest("/user/login", "POST", confirmReq.toString());
        assertEquals(HttpURLConnection.HTTP_UNAUTHORIZED, confirmRes.statusCode());
    }
}
