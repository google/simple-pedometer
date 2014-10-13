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
 * Receives sensor updates and alerts a StepListener when a step has been detected.
 */
public class SimpleStepDetector {

  private static final int ACCEL_RING_SIZE = 50;
  private static final int VEL_RING_SIZE = 10;
  private static final float STEP_THRESHOLD = 4f;
  private static final int STEP_DELAY_NS = 250000000;

  private int accelRingCounter = 0;
  private float[] accelRingX = new float[ACCEL_RING_SIZE];
  private float[] accelRingY = new float[ACCEL_RING_SIZE];
  private float[] accelRingZ = new float[ACCEL_RING_SIZE];
  private int velRingCounter = 0;
  private float[] velRing = new float[VEL_RING_SIZE];
  private long lastStepTimeNs = 0;
  private float oldVelocityEstimate = 0;

  private StepListener listener;

  public void registerListener(StepListener listener) {
    this.listener = listener;
  }

  /**
   * Accepts updates from the accelerometer.
   */
  public void updateAccel(long timeNs, float x, float y, float z) {
    float[] currentAccel = new float[3];
    currentAccel[0] = x;
    currentAccel[1] = y;
    currentAccel[2] = z;

    // First step is to update our guess of where the global z vector is.
    accelRingCounter++;
    accelRingX[accelRingCounter % ACCEL_RING_SIZE] = currentAccel[0];
    accelRingY[accelRingCounter % ACCEL_RING_SIZE] = currentAccel[1];
    accelRingZ[accelRingCounter % ACCEL_RING_SIZE] = currentAccel[2];

    float[] worldZ = new float[3];
    worldZ[0] = SensorFusionMath.sum(accelRingX) / Math.min(accelRingCounter, ACCEL_RING_SIZE);
    worldZ[1] = SensorFusionMath.sum(accelRingY) / Math.min(accelRingCounter, ACCEL_RING_SIZE);
    worldZ[2] = SensorFusionMath.sum(accelRingZ) / Math.min(accelRingCounter, ACCEL_RING_SIZE);

    float normalization_factor = SensorFusionMath.norm(worldZ);

    worldZ[0] = worldZ[0] / normalization_factor;
    worldZ[1] = worldZ[1] / normalization_factor;
    worldZ[2] = worldZ[2] / normalization_factor;

    // Next step is to figure out the component of the current acceleration
    // in the direction of world_z and subtract gravity's contribution
    float currentZ = SensorFusionMath.dot(worldZ, currentAccel) - normalization_factor;
    velRingCounter++;
    velRing[velRingCounter % VEL_RING_SIZE] = currentZ;

    float velocityEstimate = SensorFusionMath.sum(velRing);

    if (velocityEstimate > STEP_THRESHOLD && oldVelocityEstimate <= STEP_THRESHOLD
        && (timeNs - lastStepTimeNs > STEP_DELAY_NS)) {
      listener.step(timeNs);
      lastStepTimeNs = timeNs;
    }
    oldVelocityEstimate = velocityEstimate;
  }
}