// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
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
import java.util.List;
import java.util.stream.Collectors;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {
    private int numOfComments = 10;

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Query query = new Query("DataComment").addSort("dateCreated", SortDirection.DESCENDING);
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        PreparedQuery results = datastore.prepare(query);
        List<Entity> entityResults = results.asList(FetchOptions.Builder.withLimit(numOfComments));
   
        List<DataComment> dataComments = new ArrayList<>();
        for (Entity entity : entityResults) {
            long id = entity.getKey().getId();
            String message = (String) entity.getProperty("message");
            String creator = (String) entity.getProperty("creator");
            String dateCreated = (String) entity.getProperty("dateCreated");
            
            DataComment dataComment = new DataComment(id, creator, message, dateCreated);
            dataComments.add(dataComment);
        }
        
        response.setContentType("application/json;");
        String json = convertToJsonByGson(dataComments);
        response.getWriter().println(json);
    }
    
    private String convertToJsonByGson(List jsonItem) {
        Gson gson = new Gson();
        String json = gson.toJson(jsonItem);
        return json;
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String message = request.getParameter("text-input");
        String creator = "Unknown";
        String dateCreated = "June 2nd";
        Entity messageEntity = new Entity("DataComment");
        messageEntity.setProperty("message", message);
        messageEntity.setProperty("creator", creator);
        messageEntity.setProperty("dateCreated", dateCreated);

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        datastore.put(messageEntity);
        response.sendRedirect("/index.html");
    }
    
}
