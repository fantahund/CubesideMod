package de.fanta.cubeside.util.ChatSkullAPI;

import de.fanta.cubeside.CubesideClientFabric;
import de.fanta.cubeside.util.ChatUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.net.URL;

public class SkullClass {

  String playerName;
  
  SkullClass(String playerName) {
    this.playerName = playerName;
  }

  public void setItemLore() {
    BufferedImage imageToSend = null;
    try {
      imageToSend = ImageIO.read(newURL(playerName));
    } catch (Exception e) {
      ChatUtils.sendErrorMessage("Kopf von " + playerName + " konnte nicht gefunden werden.");
      CubesideClientFabric.LOGGER.error("Kopf " + playerName + " konnte nicht gefunden werden.", e);
    }
    (new Message(imageToSend, 8, '█')).setItemLore();
  }
  
  private static URL newURL(String name) throws Exception {
    String url = "https://mineskin.eu/helm/%pname%/8.png";
    url = url.replace("%pname%", name);
    return new URL(url);
  }
}
