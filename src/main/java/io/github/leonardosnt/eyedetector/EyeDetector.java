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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.objdetect.CascadeClassifier;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;

public class EyeDetector {
  private static final String EYE_CASCADE_PATH = Paths.get(System.getProperty("user.dir"), "haarcascade_eye.xml").toString();

  public static String detect(byte[] imgData) throws IOException {
    File tempImage = File.createTempFile("tmpImg", "");
    
    JsonObject jsonResponse = Json.object();
    JsonArray eyesJsonArray = (JsonArray) Json.array();
    jsonResponse.add("eyes", eyesJsonArray).toString();
    
    try {
      Files.write(tempImage.toPath(), imgData);

      CascadeClassifier eyeClassifier = new CascadeClassifier(EYE_CASCADE_PATH);
      Mat image = Imgcodecs.imread(tempImage.getAbsolutePath());

      MatOfRect eyeDetections = new MatOfRect();
      MatOfInt numDetections = new MatOfInt(1);

      eyeClassifier.detectMultiScale2(image, eyeDetections, numDetections);

      Rect[] eyes = eyeDetections.toArray();
      
      if (eyes.length == 0) {
        return jsonResponse.toString();
      }
      
      eyesJsonArray.add(Json.object().add("x", eyes[0].x).add("y", eyes[0].y));
      eyesJsonArray.add(Json.object().add("x", eyes[1].x).add("y", eyes[1].y));
    } catch (Exception ex) {
      ex.printStackTrace();
    } finally {
      tempImage.delete();
    }
    return jsonResponse.toString();
  }
 
}