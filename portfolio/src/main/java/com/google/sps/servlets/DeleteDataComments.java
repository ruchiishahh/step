package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;

import com.google.gson.Gson;
import com.google.sps.data.DataComment;

import java.io.IOException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@WebServlet("/deletedatacomments")
public class DeleteDataComments extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("text/html;");
    response.getWriter().println("Temporary");
  }


  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Query query = new Query("DataComment").addSort("dateCreated", SortDirection.DESCENDING);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);
    for (Entity entity : results.asIterable()) {
        datastore.delete(entity.getKey());
    }

    response.sendRedirect("/index.html");
  }

}