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

// Declaring a WebServlet called SingleStarServlet, which maps to url "/api/single-star"
@WebServlet(name = "UpdateCartItem", urlPatterns = "/api/cartUpdate")
public class UpdateCartItem extends HttpServlet {
    private static final long serialVersionUID = 2L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setContentType("application/json"); // Response mime type

        String movieId = request.getParameter("id");
        String itemNumber = request.getParameter("item");

        PrintWriter out = response.getWriter();

        try {
                HttpSession session = request.getSession();
                HashMap<String, JsonObject> cartItem = (HashMap<String, JsonObject>) session.getAttribute("cartItem");

                cartItem.get(movieId).addProperty("quantity", itemNumber);

                JsonArray jsonArray = new JsonArray();
                for (JsonObject i : cartItem.values()) {
                    jsonArray.add(i);
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

            // set reponse status to 500 (Internal Server Error)
            response.setStatus(500);
        }
        out.close();
        //close it;
    }
}
