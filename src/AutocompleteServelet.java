import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet("/autocomplete")
public class AutocompleteServelet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    public AutocompleteServelet() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            // setup the response json arrray
            JsonArray jsonArray = new JsonArray();

            // get the query string from parameter
            String query = request.getParameter("query");

            // return the empty json array if query is null or empty
            if (query == null || query.trim().isEmpty()) {
                response.getWriter().write(jsonArray.toString());
                return;
            }

            String [] arrTitle = query.split("\\s+");
            query = "";
            for(String s: arrTitle){
                query += "+" + s + "* ";
            }

            Connection dbcon = dataSource.getConnection();
//            Context initCtx = new InitialContext();
//
//            Context envCtx = (Context) initCtx.lookup("java:comp/env");
//
//            // Look up our data source
//            DataSource ds = (DataSource) envCtx.lookup("jdbc/moviedb");
//
//            Connection dbcon = ds.getConnection();

            String sqlQuery = "select id, title from movies where match (title) against (? in boolean mode) limit 10; ";

            PreparedStatement statement = dbcon.prepareStatement(sqlQuery);

            statement.setString(1, query);
            ResultSet rs = statement.executeQuery();

            while(rs.next()){
                String movieId = rs.getString("id");
                String title = rs.getString("title");
                jsonArray.add(generateJsonObject(movieId, title));
            }

            response.getWriter().write(jsonArray.toString());
            rs.close();
            statement.close();
            dbcon.close();
        } catch (Exception e) {
            System.out.println(e);
            response.sendError(500, e.getMessage());
        }
    }

    private static JsonObject generateJsonObject(String movieId, String title) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("value", title);

        JsonObject additionalDataJsonObject = new JsonObject();
        additionalDataJsonObject.addProperty("movieId", movieId);

        jsonObject.add("data", additionalDataJsonObject);
        return jsonObject;
    }
}


