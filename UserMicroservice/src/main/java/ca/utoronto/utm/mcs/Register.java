package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Register extends Endpoint {

    /**
     * POST /user/register
     * @body name, email, password
     * @return 200, 400, 500
     * Register a user into the system using the given information.
     */

    @Override
    public void handlePost(HttpExchange r) throws IOException, JSONException {
        // TODO
        System.out.println("start");
        // check if request url isn't malformed
        String[] splitUrl = r.getRequestURI().getPath().split("/");
        if (splitUrl.length != 3) {
            this.sendStatus(r, 400);
            return;
        }

        try {
            String body = Utils.convert(r.getRequestBody());
            JSONObject deserialized = new JSONObject(body);

            System.out.println("start");
            String email = null;
            String name = null;
            String password = null;

            // check what values are present
            if (deserialized.has("email")) {
                if (deserialized.get("email").getClass() != String.class) {
                    this.sendStatus(r, 400);
                    return;
                }
                email = deserialized.getString("email");
            }
            if (deserialized.has("name")) {
                if (deserialized.get("name").getClass() != String.class) {
                    this.sendStatus(r, 400);
                    return;
                }
                name = deserialized.getString("name");
            }
            if (deserialized.has("password")) {
                if (deserialized.get("password").getClass() != String.class) {
                    this.sendStatus(r, 400);
                    return;
                }
                password = deserialized.getString("password");
            }

            // if all the variables are still null then there's no variables in request so retrun 400
            if (email == null || name == null || password == null) {
                this.sendStatus(r, 400);
                return;
            }

            //check if email is already registered
            ResultSet rs1;
            boolean resultHasNext;
            try {
                rs1 = this.dao.getUsersByEmail(email);
                resultHasNext = rs1.next();
            }
            catch (SQLException e) {
                e.printStackTrace();
                this.sendStatus(r, 500);
                return;
            }

            //if email already exists
            if (resultHasNext) {
                this.sendStatus(r, 409);
                return;
            }

            // update db, return 500 if error
            try {
                rs1 = this.dao.addUser(name, email, password);
                System.out.println("hello");
            } catch (SQLException e) {
                e.printStackTrace();
                this.sendStatus(r, 500);
                return;
            }

            //Piazza @175 said to not return it
//            String uid = rs1.getString("uid");
//
//            JSONObject resp = new JSONObject();
//            resp.put("uid",uid);
//            this.sendResponse(r,resp, 200);

            this.sendStatus(r, 200);
        }
        catch(Exception e){
            e.printStackTrace();
            this.sendStatus(r, 500);
        }
    }


}
