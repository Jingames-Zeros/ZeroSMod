package org.darkoro.zerosmod.finisher;

public enum FinisherCameraPreset {
  ORBIT,
  SHOULDER,
  SIDE,
  INTRO,
  ORBIT_TARGET;

  public static FinisherCameraPreset byName(String name) {
    if (name == null) {
      return null;
    }

    for (FinisherCameraPreset preset : values()) {
      if (preset.name().equalsIgnoreCase(name.trim())) {
        return preset;
      }
    }
    return null;
  }

  public static FinisherCameraPreset byId(int id) {
    FinisherCameraPreset[] presets = values();
    return id >= 0 && id < presets.length ? presets[id] : ORBIT;
  }

  public static String[] names() {
    FinisherCameraPreset[] presets = values();
    String[] names = new String[presets.length];
    for (int i = 0; i < presets.length; i++) {
      names[i] = presets[i].name().toLowerCase();
    }
    return names;
  }
}
