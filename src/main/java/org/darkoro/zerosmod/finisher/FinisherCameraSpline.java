package org.darkoro.zerosmod.finisher;

/**
 * Interpolation for camera paths: a Catmull-Rom spline through keyframes at uneven tick spacing, so the camera
 * passes through every keyframe without snapping at them.
 */
public final class FinisherCameraSpline {

  private FinisherCameraSpline() {}

  /**
   * @param ticks keyframe ticks, ascending and distinct
   * @param values keyframe values, one per tick
   * @param time tick to sample; held at the first or last value outside the keyframes
   * @return the interpolated value
   */
  public static double sample(double[] ticks, double[] values, double time) {
    int last = ticks.length - 1;
    if (last == 0 || time <= ticks[0]) {
      return values[0];
    }
    if (time >= ticks[last]) {
      return values[last];
    }

    int i = segment(ticks, time);
    double span = ticks[i + 1] - ticks[i];
    double s = (time - ticks[i]) / span;
    double s2 = s * s;
    double s3 = s2 * s;
    return (2.0D * s3 - 3.0D * s2 + 1.0D) * values[i]
        + (s3 - 2.0D * s2 + s) * span * tangent(ticks, values, i)
        + (-2.0D * s3 + 3.0D * s2) * values[i + 1]
        + (s3 - s2) * span * tangent(ticks, values, i + 1);
  }

  /**
   * @return index of the keyframe that starts the segment containing {@code time}, clamped to the valid segments
   */
  public static int segment(double[] ticks, double time) {
    int i = 0;
    while (i < ticks.length - 2 && time >= ticks[i + 1]) {
      i++;
    }
    return i;
  }

  /**
   * Unwraps angles in degrees so each one is within 180 of the one before, so interpolating them turns the
   * short way around (350 to 10 becomes 350 to 370).
   */
  public static double[] unwrapDegrees(double[] angles) {
    double[] unwrapped = new double[angles.length];
    for (int i = 0; i < angles.length; i++) {
      unwrapped[i] = i == 0 ? angles[0] : unwrapped[i - 1] + wrapDegrees(angles[i] - angles[i - 1]);
    }
    return unwrapped;
  }

  /** @return the angle in degrees wrapped into [-180, 180) */
  public static double wrapDegrees(double angle) {
    double wrapped = (angle + 180.0D) % 360.0D;
    return (wrapped < 0.0D ? wrapped + 360.0D : wrapped) - 180.0D;
  }

  // Catmull-Rom tangent for uneven spacing; one-sided at the ends
  private static double tangent(double[] ticks, double[] values, int i) {
    int last = ticks.length - 1;
    int before = Math.max(0, i - 1);
    int after = Math.min(last, i + 1);
    return (values[after] - values[before]) / (ticks[after] - ticks[before]);
  }
}
