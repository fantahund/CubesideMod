package de.fanta.cubeside.util;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class ChatInfo {
    private final String currentChannelName;
    private final String currentPrivateChat;
    private final String currentResponsePartner;
    private final MutableText currentChannelColor;
    private final MutableText currentPrivateChatPrefix;
    private final MutableText currentResponsePartnerPrefix;

    public ChatInfo(String currentChannelName, String currentPrivateChat, String currentResponsePartner, MutableText currentChannelColor, MutableText currentPrivateChatPrefix, MutableText currentResponsePartnerPrefix) {
        this.currentChannelName = currentChannelName;
        this.currentPrivateChat = currentPrivateChat;
        this.currentResponsePartner = currentResponsePartner;
        this.currentChannelColor = currentChannelColor;
        this.currentPrivateChatPrefix = currentPrivateChatPrefix;
        this.currentResponsePartnerPrefix = currentResponsePartnerPrefix;
    }

    public String getCurrentChannelName() {
        return currentChannelName;
    }

    public String getCurrentPrivateChat() {
        return currentPrivateChat;
    }

    public String getCurrentResponsePartner() {
        return currentResponsePartner;
    }

    public Text getCurrentChannelColor() {
        return currentChannelColor;
    }

    public Text getCurrentPrivateChatPrefix() {
        return currentPrivateChatPrefix;
    }

    public Text getCurrentResponsePartnerPrefix() {
        return currentResponsePartnerPrefix;
    }

    public boolean isPrivatChat() {
        return !currentPrivateChat.equals("");
    }

    public MutableText getColoredChannelText() {
        return currentChannelColor.getSiblings().isEmpty() ? Text.of(currentChannelName).copy() : currentChannelColor.getSiblings().get(currentChannelColor.getSiblings().size() - 1).copy().append(currentChannelName);
    }
    public MutableText getColoredPrivatText() {
        return currentPrivateChatPrefix.getSiblings().isEmpty() ? Text.of(currentPrivateChat).copy() : currentPrivateChatPrefix.getSiblings().get(currentPrivateChatPrefix.getSiblings().size() - 1).copy().append(currentPrivateChat);
    }

    public boolean hasResponsePlayer() {
        return !currentResponsePartner.equals("");
    }

    public MutableText getColoredResponseText() {
        return currentResponsePartnerPrefix.getSiblings().isEmpty() ? Text.of(currentResponsePartner).copy() : currentResponsePartnerPrefix.getSiblings().get(currentResponsePartnerPrefix.getSiblings().size() - 1).copy().append(currentResponsePartner);
    }
}
