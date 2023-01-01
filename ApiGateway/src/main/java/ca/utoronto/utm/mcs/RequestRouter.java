package ca.utoronto.utm.mcs;

import ca.utoronto.utm.mcs.Utils;
import com.sun.net.httpserver.HttpExchange;

import com.sun.net.httpserver.HttpHandler;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class RequestRouter implements HttpHandler {

    /**
     * You may add and/or initialize attributes here if you
     * need.
     */
    public RequestRouter() {

    }

    public void writeOutputStream(HttpExchange r, String response) throws IOException {
        OutputStream os = r.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    public void sendResponse(HttpExchange r, String response, int statusCode) throws JSONException, IOException {
        r.sendResponseHeaders(statusCode, response.length());
        this.writeOutputStream(r, response);
    }


    @Override
    public void handle(HttpExchange r) throws IOException {
        HttpClient client = HttpClient.newBuilder().build();
        String[] params = r.getRequestURI().toString().split("/");
        String body = Utils.convert(r.getRequestBody());
        JSONObject deserialized = new JSONObject();
        if(!(body.isEmpty() || body.equals(""))) {
            try {
                deserialized = new JSONObject(body);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }

        String microservice = params[1];

        System.out.println(microservice);
        HttpResponse response;
        HttpRequest request;
        if (microservice.equals("trip")) {
            request = HttpRequest.newBuilder()
                    .uri(URI.create("http://tripinfomicroservice:8000" + r.getRequestURI().toString()))
                    .method(r.getRequestMethod(), HttpRequest.BodyPublishers.ofString(deserialized.toString()))
                    .build();
            try {
                response = client.send(request, HttpResponse.BodyHandlers.ofString());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        } else if (microservice.equals("user")) {
            request = HttpRequest.newBuilder()
                    .uri(URI.create("http://usermicroservice:8000" + r.getRequestURI().toString()))
                    .method(r.getRequestMethod(), HttpRequest.BodyPublishers.ofString(deserialized.toString()))
                    .build();
            try {
                response = client.send(request, HttpResponse.BodyHandlers.ofString());

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        } else {
            request = HttpRequest.newBuilder()
                    .uri(URI.create("http://locationmicroservice:8000" + r.getRequestURI().toString()))
                    .method(r.getRequestMethod(), HttpRequest.BodyPublishers.ofString(deserialized.toString()))
                    .build();
            try {

                response = client.send(request, HttpResponse.BodyHandlers.ofString());

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        System.out.println(response.body().toString());
        try {
            this.sendResponse(r, response.body().toString(), response.statusCode());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }


    }


}
