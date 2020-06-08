package com.google.sps.servlets;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/user-login")
public class UserLogin extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("text/html");

    UserService userService = UserServiceFactory.getUserService();
    String urlToRedirectToAfterUserLogsInOrOut = "/";
    if (userService.isUserLoggedIn()) {
      String userEmail = userService.getCurrentUser().getEmail();
      String logoutUrl = userService.createLogoutURL(urlToRedirectToAfterUserLogsInOrOut);
      response.getWriter().println("true");

    } else {
      String loginUrl = userService.createLoginURL(urlToRedirectToAfterUserLogsInOrOut);
      response.getWriter().println("false");
    }
  }
}
