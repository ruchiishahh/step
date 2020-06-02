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

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {
    private String name = "Ruchi";
    

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    //response.setContentType("text/html;");
    //response.getWriter().println("Hello " + name + "!");

    ArrayList<String> sampleNames = new ArrayList<String>();
    sampleNames.add("Lebron James");
    sampleNames.add("Kobe Bryant");
    sampleNames.add("Michael Jordan");

    String json = convertToJson(sampleNames);

    // Send the JSON as the response
    response.setContentType("application/json;");
    response.getWriter().println(json);
  }

  /**
   * Converts a ServerStats instance into a JSON string using manual String concatentation.
   */
  private String convertToJson(ArrayList<String> list) {
    String json = "{";
    json += "\"first name\": ";
    json += "\"" + list.get(0) + "\"";
    json += ", ";
    json += "\"second name\": ";
    json += "\"" + list.get(1) + "\"";
    json += ", ";
    json += "\"third name\": ";
    json += "\"" + list.get(2) + "\""; 
    json += "}";
    return json;
  }
}
