/*
 * Copyright 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.android.apps.simplepedometer;

/**
 * A collection of matrix and vector operations used specifically for sensor
 * fusion.  These are purposefully specific so as to be fast.
 */
public class SensorFusionMath {

  private SensorFusionMath() {
  }

  public static float sum(float[] array) {
    float retval = 0;
    for (int i = 0; i < array.length; i++) {
      retval += array[i];
    }
    return retval;
  }

  public static float[] cross(float[] arrayA, float[] arrayB) {
    float[] retArray = new float[3];
    retArray[0] = arrayA[1] * arrayB[2] - arrayA[2] * arrayB[1];
    retArray[1] = arrayA[2] * arrayB[0] - arrayA[0] * arrayB[2];
    retArray[2] = arrayA[0] * arrayB[1] - arrayA[1] * arrayB[0];
    return retArray;
  }

  public static float norm(float[] array) {
    float retval = 0;
    for (int i = 0; i < array.length; i++) {
      retval += array[i] * array[i];
    }
    return (float) Math.sqrt(retval);
  }

  // Note: only works with 3D vectors.
  public static float dot(float[] a, float[] b) {
    float retval = a[0] * b[0] + a[1] * b[1] + a[2] * b[2];
    return retval;
  }

  public static float[] normalize(float[] a) {
    float[] retval = new float[a.length];
    float norm = norm(a);
    for (int i = 0; i < a.length; i++) {
      retval[i] = a[i] / norm;
    }
    return retval;
  }

}