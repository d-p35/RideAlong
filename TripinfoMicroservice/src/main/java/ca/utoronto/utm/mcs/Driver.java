package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class Driver extends Endpoint {

    /**
     * GET /trip/driver/:uid
     * @param uid
     * @return 200, 400, 404
     * Get all trips driver with the given uid has.
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
            String duid = String.valueOf(params[3]);

            
            ArrayList<JSONObject> tripsArr = this.dao.getTripInfoDriver(duid);


            JSONObject trips = new JSONObject().put("trips",tripsArr);

            JSONObject finalans = new JSONObject().put("data",trips);
            this.sendResponse(r, finalans, 200);
        } catch (Exception e) {
            e.printStackTrace();
            this.sendStatus(r, 500);
        }
    }
    }

