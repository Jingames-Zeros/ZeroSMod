package org.darkoro.zerosmod.config;

import java.io.File;
import java.util.EnumMap;
import java.util.Map;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import org.darkoro.guiapi.guis.clientside.Spirit.SpiritHudComponent;

public final class SpiritConfig {

  private final File spiritConfigDir;
  private static Configuration spiritConfig;
  private final SpiritHudComponent components = new SpiritHudComponent();

  public SpiritConfig(String path){
    this.spiritConfigDir = new File(path);
  }
  public SpiritHudComponent get(){
    return components;
  }
  public void loadConfig() {
    Configuration cfg = new Configuration(spiritConfigDir);
    cfg.load();
    update(cfg,true);

  }

  public void saveConfig() {
    Configuration cfg = new Configuration(spiritConfigDir);
    update(cfg,false);
    cfg.save();
  }

  private void update(Configuration cfg, boolean load){
    Property p;
    String cat = "Spirit Control Hud";

    p = cfg.get(cat,"enabled",components.isEnabled());
    if (load) components.setEnabled(p.getBoolean());
    else p.set(components.isEnabled());

    p = cfg.get(cat,"showText",components.isShowText());
    if (load) components.setShowText(p.getBoolean());
    else p.set(components.isShowText());

    p = cfg.get(cat,"scale",components.getScale());
    if (load) components.setScale((float) p.getDouble());
    else p.set(components.getScale());

    p = cfg.get(cat,"offsetX",components.getOffsetX());
    if (load) components.setOffsetX(p.getInt());
    else p.set(components.getOffsetX());

    p = cfg.get(cat,"offsetY",components.getOffsetY());
    if (load) components.setOffsetY(p.getInt());
    else p.set(components.getOffsetY());

    p = cfg.get(cat,"textOffsetX",components.getTextOffsetX());
    if (load) components.setTextOffsetX(p.getInt());
    else p.set(components.getOffsetX());

    p = cfg.get(cat,"textOffsetY",components.getTextOffsetY());
    if (load) components.setTextOffsetY(p.getInt());
    else p.set(components.getTextOffsetY());

    p = cfg.get(cat,"textScale",components.getTextScale());
    if (load) components.setTextScale((float) p.getDouble());
    else p.set(components.getTextScale());

    p = cfg.get(cat,"color",components.getColor());
    if (load) components.setColor(p.getInt());
    else p.set(components.getColor());
  }

}
