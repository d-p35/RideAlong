package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class Confirm extends Endpoint {

    /**
     * POST /trip/confirm
     *
     * @return 200, 400
     * Adds trip info into the database after trip has been requested.
     * @body driver, passenger, startTime
     */

    @Override
    public void handlePost(HttpExchange r) throws IOException, JSONException {
        String[] splitUrl = r.getRequestURI().getPath().split("/");
        if (splitUrl.length != 3) {
            this.sendStatus(r, 400);
            return;
        }

        try {
            JSONObject body = new JSONObject(Utils.convert(r.getRequestBody()));
            String fields[] = {"driver", "passenger"};
            Class<?> fieldClasses[] = {String.class, String.class};
            if (!validateFields(body, fields, fieldClasses)) {
                this.sendStatus(r, 400);
                return;
            }

            String driver = body.getString("driver");
            String passenger = body.getString("passenger");
            int startTime;
            try {
                if (body.has("startTime")) {
                    startTime = body.getInt("startTime");
                }
                else{
                    this.sendStatus(r, 400);
                    return;
                }
            }
            catch (Exception e){
                e.printStackTrace();
                this.sendStatus(r, 400);
                return;
            }

            String _id;

            try {
               _id = this.dao.confirmTrip(driver, passenger, startTime);
            }
            catch (Exception e){
                e.printStackTrace();
                this.sendStatus(r, 500);
                return;
            }

            
            JSONObject response = new JSONObject();
            response.put("_id", _id);

            JSONObject answer = new JSONObject();
            answer.put("data", response);

            System.out.println(_id);
            this.sendResponse(r, answer, 200);
        }

        catch (Exception e){
            e.printStackTrace();
            this.sendStatus(r, 500);
        }
    }
}
