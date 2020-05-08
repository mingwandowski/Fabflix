import com.google.gson.JsonObject;
import org.jasypt.util.password.StrongPasswordEncryptor;

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

@WebServlet(name = "EmpLoginServlet", urlPatterns = "/api/employee-login")
public class EmpLoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public String getServletInfo() {
        return "Servlet connects to MySQL database and displays result of a SELECT";
    }

    // Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        PrintWriter out = response.getWriter();

        String gRecaptchaResponse = request.getParameter("g-recaptcha-response");
        System.out.println("gRecaptchaResponse=" + gRecaptchaResponse);

        // Verify reCAPTCHA
        try {
            RecaptchaVerifyUtils.verify(gRecaptchaResponse);
        } catch (Exception e) {
            JsonObject responseJsonObject = new JsonObject();
            responseJsonObject.addProperty("status", "fail");
            responseJsonObject.addProperty("message", e.getMessage());
            out.write(responseJsonObject.toString());
            out.close();
            return;
        }

        String inputEmail = request.getParameter("email");
        String inputPassword = request.getParameter("password");

        try {
            // Get a connection from dataSource
            Connection dbcon = dataSource.getConnection();

            String query = "select * from employees where email = ?;";

            PreparedStatement statement = dbcon.prepareStatement(query);
            statement.setString(1, inputEmail);
            ResultSet rs = statement.executeQuery();

            JsonObject responseJsonObject = new JsonObject();

            // no customer with the input email
            if(!rs.next()){
                // Login fail
                responseJsonObject.addProperty("status", "fail");
                responseJsonObject.addProperty("message", "email " + inputEmail + " doesn't exist");
            }else{
                String email = rs.getString("email");
                String encryptedPassword = rs.getString("password");
                String fullName = rs.getString("fullname");

                boolean success = new StrongPasswordEncryptor().checkPassword(inputPassword, encryptedPassword);
                // input password noes not match
                if(!success){
                    // Login fail
                    responseJsonObject.addProperty("status", "fail");
                    responseJsonObject.addProperty("message", "incorrect password");
                }else{
                    // Login success
                    // set this user into the session
                    request.getSession().setAttribute("employee", new Employee(email, encryptedPassword, fullName));
                    responseJsonObject.addProperty("status", "success");
                    responseJsonObject.addProperty("message", "success");
                }
            }

            out.write(responseJsonObject.toString());

            rs.close();
            statement.close();
            dbcon.close();
            out.close();
        } catch (Exception e) {

            // write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());

            // set reponse status to 500 (Internal Server Error)
            response.setStatus(500);

            out.close();
        }
    }
}
