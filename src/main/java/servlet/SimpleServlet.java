package servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import acme.SimpleTinyURL;
import org.apache.log4j.Logger;

@WebServlet(
        name = "MyServlet",
        urlPatterns = {"/simple/*"}
)
public class SimpleServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(SimpleServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        String shortURL = request.getPathInfo();  // todo: validate shortURL
        if (shortURL != null)
            shortURL = shortURL.substring(1);

        String longURL = SimpleTinyURL.getInstance().decode(shortURL);
        if (longURL == null) {
            sendError(response, response.getWriter(), HttpServletResponse.SC_BAD_REQUEST, "No such short URL " + shortURL );
            return;
        }
        response.sendRedirect("http://" + longURL);
    }

    @Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {
        try {
            response.setContentType("application/json");
            String longURL = request.getParameter("url");  //todo: validate longURL
            String shortURL = SimpleTinyURL.getInstance().encode(longURL);

            String res = "{ \"id\": \"" + shortURL + "\" }";
            response.getWriter().println(res);
        } catch (Exception e) {
            sendError(response, response.getWriter(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }


    private static final void sendError(final HttpServletResponse response, final PrintWriter out,
                                        final int status, final String msg) {
        response.setContentType("text/plain");
        response.setStatus(status);
        out.println(msg);
    }
}