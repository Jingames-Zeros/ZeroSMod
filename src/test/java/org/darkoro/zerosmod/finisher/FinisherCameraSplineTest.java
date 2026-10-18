package org.darkoro.zerosmod.finisher;

/** Checks the camera path maths, runnable without launching Minecraft. */
public final class FinisherCameraSplineTest {
  private static int checks;

  public static void main(String[] args) {
    double[] ticks = {0, 40, 80};
    double[] values = {0, 4, 1};

    check(FinisherCameraSpline.sample(ticks, values, 0) == 0, "starts on the first keyframe");
    check(near(FinisherCameraSpline.sample(ticks, values, 40), 4), "passes through a middle keyframe");
    check(FinisherCameraSpline.sample(ticks, values, 80) == 1, "ends on the last keyframe");
    check(FinisherCameraSpline.sample(ticks, values, -5) == 0, "holds the first keyframe before it");
    check(FinisherCameraSpline.sample(ticks, values, 200) == 1, "holds the last keyframe after it");
    check(FinisherCameraSpline.sample(new double[] {10}, new double[] {3}, 5) == 3, "a single keyframe stays put");

    // Smooth: no jump in value or slope across a keyframe
    double before = FinisherCameraSpline.sample(ticks, values, 39.999);
    double after = FinisherCameraSpline.sample(ticks, values, 40.001);
    check(Math.abs(after - before) < 0.01, "continuous across a keyframe");
    double slopeBefore = (FinisherCameraSpline.sample(ticks, values, 39.99) - FinisherCameraSpline.sample(ticks, values, 39.98)) / 0.01;
    double slopeAfter = (FinisherCameraSpline.sample(ticks, values, 40.02) - FinisherCameraSpline.sample(ticks, values, 40.01)) / 0.01;
    check(Math.abs(slopeAfter - slopeBefore) < 0.01, "slope continuous across a keyframe");

    double[] straight = FinisherCameraSpline.unwrapDegrees(new double[] {0, 0, 0});
    check(straight[2] == 0, "unwrap keeps equal angles");
    double[] wrap = FinisherCameraSpline.unwrapDegrees(new double[] {350, 10, 170});
    check(near(wrap[1], 370), "turns 20 degrees the short way instead of 340 back");
    check(near(wrap[2], 530), "keeps turning the short way across several keyframes");
    check(near(FinisherCameraSpline.wrapDegrees(190), -170), "wraps into [-180, 180)");
    check(near(FinisherCameraSpline.wrapDegrees(-540), -180), "wraps large negative angles");

    check(FinisherCameraSpline.segment(ticks, 0) == 0 && FinisherCameraSpline.segment(ticks, 39) == 0, "first segment");
    check(FinisherCameraSpline.segment(ticks, 40) == 1 && FinisherCameraSpline.segment(ticks, 80) == 1, "last segment");

    check(FinisherCameraEase.byName("inOut") == FinisherCameraEase.IN_OUT, "parses inOut");
    check(FinisherCameraEase.byName("in_out") == FinisherCameraEase.IN_OUT, "parses in_out");
    check(FinisherCameraEase.byName(" LINEAR ") == FinisherCameraEase.LINEAR, "parses ignoring case and spaces");
    check(FinisherCameraEase.byName("bounce") == null, "rejects unknown eases");
    for (FinisherCameraEase ease : FinisherCameraEase.values()) {
      check(ease.apply(0) == 0 && ease.apply(1) == 1, ease + " runs from 0 to 1");
    }
    check(FinisherCameraEase.IN.apply(0.5) < 0.5 && FinisherCameraEase.OUT.apply(0.5) > 0.5, "in starts slow, out ends slow");
    check(near(FinisherCameraEase.IN_OUT.apply(0.5), 0.5), "inOut is symmetric");

    System.out.println("FinisherCameraSplineTest: " + checks + " checks passed");
  }

  private static boolean near(double actual, double expected) {
    return Math.abs(actual - expected) < 1.0E-9;
  }

  private static void check(boolean condition, String message) {
    if (!condition) {
      throw new AssertionError(message);
    }
    checks++;
  }
}
