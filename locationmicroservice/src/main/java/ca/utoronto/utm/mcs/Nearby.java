package ca.utoronto.utm.mcs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.*;
import com.sun.net.httpserver.HttpExchange;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Transaction;

public class Nearby extends Endpoint {
    
    /**
     * GET /location/nearbyDriver/:uid?radius=:radius
     * @param uid, radius
     * @return 200, 400, 404, 500
     * Get drivers that are within a certain radius around a user.
     */

    @Override
    public void handleGet(HttpExchange r) throws IOException, JSONException {
        // TODO

        String[] params = r.getRequestURI().toString().split("/");
        if (params.length != 4 || params[3].isEmpty()) {
            System.out.println(params);

            this.sendStatus(r, 400);
            return;
        }
        try {
            System.out.println(params);
            String uid = params[3].split("\\?")[0];
            Integer radius = 0;
            try {
                radius = Integer.valueOf(params[3].split("radius=")[1]);
            }
            catch (Exception e){
                e.printStackTrace();
                sendStatus(r,400);
                return;
            }
            Result result = this.dao.getUserLocationByUid(uid);
            System.out.println("uid: "+uid);
            if (result.hasNext()) {
                Record user = result.next();
                System.out.println(radius);
                Double longitude = 0.0;
                Double latitude = 0.0;
                try {
                   longitude = user.get("n.longitude").asDouble();
                   latitude = user.get("n.latitude").asDouble();                }
                catch (Exception e){
                    e.printStackTrace();
                    sendStatus(r,500);
                    return;
                }


                Result nearby = this.dao.getnNearbyDrivers(latitude,longitude,radius);

                List<Record> re = nearby.list();





                JSONObject res = new JSONObject();



                for(int i=0; i<re.get(0).get(0).size(); i++){

                    String uid2 = re.get(0).get(0).get(i).get("uid").asString();
                    if(!uid2.equals(uid)) {
                        String lon = re.get(0).get(0).get(i).get("longitude").toString();
                        String lat = re.get(0).get(0).get(i).get("latitude").toString();
                        String location = re.get(0).get(0).get(i).get("street").asString();
                        List<String> resu = new ArrayList<>();
                        JSONObject bro = new JSONObject();
                        bro.put("street", location);
                        bro.put("longitude", lon);
                        bro.put("latitude", lat);

                        res.put(uid2, bro);
                    }


                }
                JSONObject j = new JSONObject().put("data",res);

                if (!res.keys().hasNext()){
                    this.sendStatus(r,404);
                    return;
                }

                this.sendResponse(r, j, 200);
                    return;
                }
                
                else {
                this.sendStatus(r, 404);
            }

        }catch (Exception e) {
            e.printStackTrace();
            this.sendStatus(r, 500);
        }

    }
}
