package ca.utoronto.utm.mcs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.*;
import com.sun.net.httpserver.HttpExchange;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Path;
import org.neo4j.driver.types.Relationship;

public class Navigation extends Endpoint {
    
    /**
     * GET /location/navigation/:driverUid?passengerUid=:passengerUid
     * @param driverUid, passengerUid
     * @return 200, 400, 404, 500
     * Get the shortest path from a driver to passenger weighted by the
     * travel_time attribute on the ROUTE_TO relationship.
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


            String driver = String.valueOf(params[3].split("\\?")[0]);

            String passengerID = String.valueOf(params[3].split("passengerUid=")[1]);
            Result result = this.dao.getshortestRoute(driver,passengerID);

            if (result.hasNext()) {
                List<Record> record = result.list();

                Path path = record.get(0).get(0).asPath();


                System.out.println(path);
                List<Node> nodeList = new ArrayList<>();
                List<Relationship> relationshipList = new ArrayList<>();

                Iterator<Node> nodeIterator = path.nodes().iterator();
                Iterator<Relationship> relationshipIterator = path.relationships().iterator();
                while (nodeIterator.hasNext()){
                    nodeList.add( nodeIterator.next());
                }
                while (relationshipIterator.hasNext()){
                    relationshipList.add( relationshipIterator.next());
                }
                JSONObject k = new JSONObject();
                k.put("street",nodeList.get(0).get("name").asString());
                k.put("is_traffic",nodeList.get(0).get("has_traffic").asBoolean());
                k.put("time",0);
                JSONObject res = new JSONObject();
                ArrayList route = new ArrayList();
                route.add(k);
                for (int i = 1; i < nodeList.size(); i++) {
                    JSONObject j = new JSONObject();
                    j.put("street",nodeList.get(i).get("name").asString());
                    j.put("is_traffic",nodeList.get(i).get("has_traffic").asBoolean());
                    j.put("time",relationshipList.get(i-1).get("travel_time").asInt());
                    route.add(j);
                }
                res.put("total_time",record.get(0).get(1) );
                res.put("route",route);

                JSONObject fin = new JSONObject();
                fin.put("data", res);


                this.sendResponse(r, fin, 200);

            } else {
                this.sendStatus(r, 404);
            }

        }catch (Exception e) {
            e.printStackTrace();
            this.sendStatus(r, 500);
        }
    }
}
