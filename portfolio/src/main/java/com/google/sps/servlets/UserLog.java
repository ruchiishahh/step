package com.google.sps.servlets;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/user-log")
public class UserLog extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    UserService userService = UserServiceFactory.getUserService();
    JsonObject jsonObject = new JsonObject();
    
    String loggedIn = "checkIfLoggedIn";
    String url = "linkForLoginLogout";

    if (userService.isUserLoggedIn()) {
        String logoutURL = userService.createLogoutURL("/");
        jsonObject.addProperty(loggedIn, true);
        jsonObject.addProperty(url, logoutURL);
    } else {
        String loginURL = userService.createLoginURL("/");
        jsonObject.addProperty(loggedIn, false);
        jsonObject.addProperty(url, loginURL);
    }
    response.setContentType("application/json");
    response.getWriter().println(jsonObject);
  }
}
