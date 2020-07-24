import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet(name = "MovieListServlet", urlPatterns = "/api/movie-list")
public class MovieListServlet extends HttpServlet {
    private static final long serialVersionUID = 2L;

    // Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        response.setContentType("application/json"); // Response mime type

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        try{
            // Get a connection from dataSource
            Connection dbcon = dataSource.getConnection();

            String type = request.getParameter("type");

            // basic parameter
            String orderBy = request.getParameter("orderBy");
            String numberOfList = request.getParameter("numberOfList");
            String page = request.getParameter("page");

            int offsetInt = (Integer.parseInt(page) * Integer.parseInt(numberOfList));
            String offset = String.valueOf(offsetInt);

            String numOfData = "0";




            ResultSet rs;
            JsonArray jsonArray = new JsonArray();
            String query = "";
            PreparedStatement statement = null;

            if("adv-search".equals(type)){
                // type = adv-search
                String title = "%" + request.getParameter("title") + "%";
                String year = request.getParameter("year").equals("") ? "%" : request.getParameter("year");
                String director = "%" + request.getParameter("director") + "%";
                String starName = "%" + request.getParameter("starName") + "%";

                String sumQuery = "select count(*) as count from (select distinct movies.*, ratings.rating " +
                        "from movies, stars_in_movies as sim, stars, ratings " +
                        "where movies.title like ? and movies.year like ? and movies.director like ? " +
                        "and movies.id = sim.movieId and sim.starId = stars.id and stars.name like ? " +
                        "and movies.id = ratings.movieId)" +
                        " as tmp ;";

                PreparedStatement tmpStatement = dbcon.prepareStatement(sumQuery);
                tmpStatement.setString(1, title);
                tmpStatement.setString(2, year);
                tmpStatement.setString(3, director);
                tmpStatement.setString(4, starName);

                ResultSet tmpRS = tmpStatement.executeQuery();
                if(tmpRS.next()){
                    numOfData = tmpRS.getString("count");
                }
                tmpRS.close();
                tmpStatement.close();


                query = "select distinct movies.*, ratings.rating " +
                        "from movies, stars_in_movies as sim, stars, ratings " +
                        "where movies.title like ? and movies.year like ? and movies.director like ? " +
                        "and movies.id = sim.movieId and sim.starId = stars.id and stars.name like ? " +
                        "and movies.id = ratings.movieId " +
                        "order by " + orderBy + " " +
                        "limit " + numberOfList + " " +
                        "offset " + offset + ";";

                // Declare our statement
                statement = dbcon.prepareStatement(query);

                // Set the parameter represented by "?" in the query to the id we get from url,
                // num 1 indicates the first "?" in the query
                statement.setString(1, title);
                statement.setString(2, year);
                statement.setString(3, director);
                statement.setString(4, starName);

            }else if("browse-genre".equals(type)){
                // type = browse-genre
                String genreId = request.getParameter("genreId");

                String sumQuery = "select count(*) as count from (select movies.*, ratings.rating " +
                        "from movies, ratings, genres_in_movies as gim " +
                        "where ratings.movieId = movies.id and movies.id = gim.movieId and gim.genreId = ?) as tmp ;";

                PreparedStatement tmpStatement = dbcon.prepareStatement(sumQuery);
                tmpStatement.setString(1, genreId);

                ResultSet tmpRS = tmpStatement.executeQuery();
                if(tmpRS.next()){
                    numOfData = tmpRS.getString("count");
                }
                tmpRS.close();
                tmpStatement.close();


                query = "select movies.*, ratings.rating " +
                        "from movies, ratings, genres_in_movies as gim " +
                        "where ratings.movieId = movies.id and movies.id = gim.movieId and gim.genreId = ? " +
                        "order by " + orderBy + " " +
                        "limit " + numberOfList + " " +
                        "offset " + offset + ";";

                // Declare our statement
                statement = dbcon.prepareStatement(query);

                // Set the parameter represented by "?" in the query to the id we get from url,
                // num 1 indicates the first "?" in the query
                statement.setString(1, genreId);


            }else if("browse-title".equals(type)){
                // type = browse-title
                String firstLetter = request.getParameter("firstLetter");

                String sumQuery = "select count(*) as count from (select  movies.*, ratings.rating " +
                        "from movies, ratings " +
                        "where ratings.movieId = movies.id and movies.title like ? ) as tmp ;";

                PreparedStatement tmpStatement = dbcon.prepareStatement(sumQuery);
                tmpStatement.setString(1, firstLetter + "%");

                ResultSet tmpRS = tmpStatement.executeQuery();
                if(tmpRS.next()){
                    numOfData = tmpRS.getString("count");
                }
                tmpRS.close();
                tmpStatement.close();


                query = "select  movies.*, ratings.rating " +
                        "from movies, ratings " +
                        "where ratings.movieId = movies.id and movies.title like ? " +
                        "order by " + orderBy + " " +
                        "limit " + numberOfList + " " +
                        "offset " + offset + ";";

                // Declare our statement
                statement = dbcon.prepareStatement(query);

                statement.setString(1, firstLetter + "%");
            }else if("browse-other".equals(type)){
                String sumQuery = "select count(*) as count from (select  movies.*, ratings.rating " +
                        "from movies, ratings " +
                        "where ratings.movieId = movies.id and movies.title regexp '^[^a-zA-Z0-9]' ) as tmp ;";

                PreparedStatement tmpStatement = dbcon.prepareStatement(sumQuery);

                ResultSet tmpRS = tmpStatement.executeQuery();
                if(tmpRS.next()){
                    numOfData = tmpRS.getString("count");
                }
                tmpRS.close();
                tmpStatement.close();

                query = "select  movies.*, ratings.rating " +
                        "from movies, ratings " +
                        "where ratings.movieId = movies.id and movies.title regexp '^[^a-zA-Z0-9]' " +
                        "order by " + orderBy + " " +
                        "limit " + numberOfList + " " +
                        "offset " + offset + ";";

                // Declare our statement
                statement = dbcon.prepareStatement(query);
            }
            // Perform the query
            rs = statement.executeQuery();

            while (rs.next()) {
                String movie_id = rs.getString("id");
                String movie_title = rs.getString("title");
                String movie_year = rs.getString("year");
                String movie_director = rs.getString("director");
                float rating = rs.getFloat("rating");

                // use movie_id to get 3 stars with new sql sentences
                String starsQuery = "select count(*) as count, tmp.* " +
                        "from (select stars.name, stars.id from stars, stars_in_movies as sim " +
                        "where stars.id = sim.starId and sim.movieId = ? ) as tmp, " +
                        "movies, stars_in_movies as sim " +
                        "where movies.id = sim.movieId and sim.starId = tmp.id " +
                        "group by sim.starId order by count desc, name asc limit 3;";

                // Declare our statement
                PreparedStatement starsStatement = dbcon.prepareStatement(starsQuery);

                starsStatement.setString(1, movie_id);
                ResultSet starsRS = starsStatement.executeQuery();
                JsonArray starsJsonArray = new JsonArray();
                while(starsRS.next()){
                    String star_name = starsRS.getString("name");
                    String star_id = starsRS.getString("id");

                    JsonObject starJsonObject = new JsonObject();
                    starJsonObject.addProperty("star_name", star_name);
                    starJsonObject.addProperty("star_id", star_id);
                    starsJsonArray.add(starJsonObject);
                }

                // use movie_id to get 3 genres with new sql sentences
                String genresQuery = "select genres.* from genres join genres_in_movies as gim " +
                        "on genres.id = gim.genreId and gim.movieId = ? order by name limit 3;";
                // Declare our statement
                PreparedStatement genresStatement = dbcon.prepareStatement(genresQuery);

                genresStatement.setString(1, movie_id);
                ResultSet genresRS = genresStatement.executeQuery();
                JsonArray genresJsonArray = new JsonArray();
                while(genresRS.next()){
                    String genre_name = genresRS.getString("name");
                    String genre_id = genresRS.getString("id");

                    JsonObject genreJsonObject = new JsonObject();
                    genreJsonObject.addProperty("genre_name", genre_name);
                    genreJsonObject.addProperty("genre_id", genre_id);
                    genresJsonArray.add(genreJsonObject);
                }

                // Create a JsonObject based on the data we retrieve from rs
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("movie_id", movie_id);
                jsonObject.addProperty("movie_title", movie_title);
                jsonObject.addProperty("movie_year", movie_year);
                jsonObject.addProperty("movie_director", movie_director);
                jsonObject.add("stars_name", starsJsonArray);
                jsonObject.add("genres_name", genresJsonArray);
                jsonObject.addProperty("rating", rating);


                jsonArray.add(jsonObject);
            }

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("orderBy", orderBy);
            jsonObject.addProperty("numberOfList", numberOfList);
            jsonObject.addProperty("page", page);
            jsonObject.addProperty("numOfData", numOfData);
            jsonObject.add("movieData", jsonArray);

            // write JSON string to output
            out.write(jsonObject.toString());
            // set response status to 200 (OK)
            response.setStatus(200);

            rs.close();
            statement.close();
            dbcon.close();
        }catch (Exception e) {
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