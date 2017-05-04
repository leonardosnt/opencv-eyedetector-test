/*
 *  Copyright (C) 2017 leonardosnt (leonardosnt@outlook.com)
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along
 *  with this program; if not, write to the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
*/

package io.github.leonardosnt.eyedetector;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.eclipsesource.json.Json;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.Response.Status;

public class HttpServer extends NanoHTTPD {
  private static final int PORT = 8080;
  private static final String USER_DIR = System.getProperty("user.dir");
  
  public HttpServer() {
    super(PORT);
    
    System.out.printf("HttpServer iniciando na porta %d\n", PORT);
  }
  
  @Override
  public Response serve(IHTTPSession session) {
    // Index
    if (session.getMethod() == Method.GET) {
      try {
        byte[] pageData = Files.readAllBytes(Paths.get(USER_DIR, "www/index.html"));
        return newFixedLengthResponse(new String(pageData, "utf-8"));
      } catch (IOException e) {
        e.printStackTrace();
        return null;
      }
    }
    
    try {
      int contentLength = Integer.parseInt(session.getHeaders().get("content-length"));
      byte[] payload = new byte[contentLength];
    
      session.getInputStream().read(payload);
      String eyeData = EyeDetector.detect(payload);
      
      Response response = newFixedLengthResponse(Status.ACCEPTED, "application/json", eyeData);
      response.addHeader("Access-Control-Allow-Origin", "*");
      
      return response;
    } catch (IOException e) {
      e.printStackTrace();
      return jsonError(e.getMessage());
    }
  }
  
  private static Response jsonError(String message) {
    return newFixedLengthResponse(Json.object().add("error", message).toString());
  }
}
