import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

@WebServlet(name = "CartDisplayServlet", urlPatterns = "/api/cartDisplay")
public class CartDisplayServlet extends HttpServlet {
    private static final long serialVersionUID = 2L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setContentType("application/json");

        PrintWriter out = response.getWriter();

        try {
            HttpSession session = request.getSession();
            HashMap<String, JsonObject> cartItem = (HashMap<String, JsonObject>) session.getAttribute("cartItem");
            JsonArray jsonArray = new JsonArray();

            if(cartItem != null){
                for (JsonObject i : cartItem.values()) {
                    jsonArray.add(i);
                }
            }

            // write JSON string to output
            out.write(jsonArray.toString());
            // set response status to 200 (OK)
            response.setStatus(200);

        } catch (Exception e) {

            // write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            // set response status to 500 (Internal Server Error)
            response.setStatus(500);
        }
        out.close();
        //close it;
    }
}
