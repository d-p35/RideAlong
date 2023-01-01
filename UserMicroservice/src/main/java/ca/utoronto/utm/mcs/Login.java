package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Login extends Endpoint {

    /**
     * POST /user/login
     * @body email, password
     * @return 200, 400, 401, 404, 500
     * Login a user into the system if the given information matches the
     * information of the user in the database.
     */

    @Override
    public void handlePost(HttpExchange r) throws IOException, JSONException {
        // TODO
        // check if request url isn't malformed
        String[] splitUrl = r.getRequestURI().getPath().split("/");
        if (splitUrl.length != 3) {
            this.sendStatus(r, 400);
            return;
        }

        try {
            JSONObject body = new JSONObject(Utils.convert(r.getRequestBody()));
            String fields[] = {"email", "password"};
            Class<?> fieldClasses[] = {String.class, String.class};
            if (!validateFields(body, fields, fieldClasses)) {
                this.sendStatus(r, 400);
                return;
            }

            String email = body.getString("email");
            String password = body.getString("password");

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

            if (!resultHasNext) {
                this.sendStatus(r, 404);
                return;
            }

            //check if email and password match up
            if(!(rs1.getString("email").equals(email) && rs1.getString("password").equals(password))){
                this.sendStatus(r, 401);
                return;
            }

            //Piazza @175 said to not return it
//
//            String uid = rs1.getString("uid");
//
//            JSONObject resp = new JSONObject();
//            resp.put("uid",uid);
//            this.sendResponse(r,resp, 200);

            this.sendStatus(r, 200);
            return;
        }
        catch(Exception e){
            e.printStackTrace();
            this.sendStatus(r, 500);
        }
    }
}
