package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;
import org.bson.types.ObjectId;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.IOException;

public class Trip extends Endpoint {

    /**
     * PATCH /trip/:_id
     * @param _id
     * @body distance, endTime, timeElapsed, totalCost
     * @return 200, 400, 404
     * Adds extra information to the trip with the given id when the 
     * trip is done. 
     */

    @Override
    public void handlePatch(HttpExchange r) throws IOException, JSONException {
        // TODO
        String[] splitUrl = r.getRequestURI().getPath().split("/");
        if (splitUrl.length != 3 || splitUrl[2].isEmpty()) {
            this.sendStatus(r, 400);
            return;
        }

        String _id = splitUrl[2];

        try {
            JSONObject body = new JSONObject(Utils.convert(r.getRequestBody()));
            double distance;
            int endTime;
            String timeElapsed;
            double discount= 0;
            double totalCost;
            double driverPayout= -1;

            try {
                distance = body.getDouble("distance");
                endTime =  body.getInt("endTime");
                timeElapsed = body.getString("timeElapsed");
                if(body.has("discount")){
                    discount = body.getDouble("discount");
                }

                totalCost = body.getDouble("totalCost");

                if(body.has("driverPayout")) {
                    driverPayout = body.getDouble("driverPayout");

                }

                ObjectId obj = new ObjectId(_id);
            }
            catch (Exception e){
                e.printStackTrace();
                this.sendStatus(r, 400);
                return;
            }

            if(driverPayout==-1){
                driverPayout = totalCost*0.65;
            }

            if (distance <0 || driverPayout<0 || totalCost<0){
                this.sendStatus(r, 400);
                return;
            }

            boolean status;

            try{
                status = this.dao.updateTrip(_id, distance, endTime, timeElapsed, discount, totalCost, driverPayout);
            }
            catch (Exception e) {
                e.printStackTrace();
                this.sendStatus(r, 500);
                return;
            }

            if(!status){
                this.sendStatus(r, 404);
                return;
            }


            this.sendStatus(r, 200);


        }
        catch (Exception e) {
            e.printStackTrace();
            this.sendStatus(r, 500);
        }
    }
}
