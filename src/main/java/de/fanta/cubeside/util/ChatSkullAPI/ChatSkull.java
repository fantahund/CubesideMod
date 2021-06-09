package de.fanta.cubeside.util.ChatSkullAPI;

public class ChatSkull  {

  public static void setItemLore(String playerName) {
    SkullClass cs = new SkullClass(playerName);
    cs.setItemLore();
  }
}
