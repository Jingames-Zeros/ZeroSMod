package org.darkoro.zerosmod.finisher;

/** Time curve of a camera path: how fast it moves through its keyframes over the shot. */
public enum FinisherCameraEase {
  LINEAR,
  IN,
  OUT,
  IN_OUT;

  /**
   * @param progress shot progress from 0 to 1
   * @return eased progress from 0 to 1
   */
  public double apply(double progress) {
    double t = Math.max(0.0D, Math.min(1.0D, progress));
    switch (this) {
      case IN:
        return t * t;
      case OUT:
        return 1.0D - (1.0D - t) * (1.0D - t);
      case IN_OUT:
        return t * t * (3.0D - 2.0D * t);
      case LINEAR:
      default:
        return t;
    }
  }

  /** Accepts {@code linear}, {@code in}, {@code out} and {@code inOut} (also {@code in_out}), ignoring case. */
  public static FinisherCameraEase byName(String name) {
    if (name == null) {
      return null;
    }

    String key = name.trim().replace("_", "").replace("-", "");
    for (FinisherCameraEase ease : values()) {
      if (ease.name().replace("_", "").equalsIgnoreCase(key)) {
        return ease;
      }
    }
    return null;
  }

  public static FinisherCameraEase byId(int id) {
    FinisherCameraEase[] eases = values();
    return id >= 0 && id < eases.length ? eases[id] : LINEAR;
  }
}
