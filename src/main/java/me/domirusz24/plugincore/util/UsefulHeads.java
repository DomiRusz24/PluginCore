package me.domirusz24.plugincore.util;

public enum UsefulHeads {
    QUARTZ_LEFT_ARROW("5f133e91919db0acefdc272d67fd87b4be88dc44a958958824474e21e06d53e6"),
    QUARTZ_RIGHT_ARROW("e3fc52264d8ad9e654f415bef01a23947edbccccf649373289bea4d149541f70");

    private String URL;

    UsefulHeads(String URL) {
        this.URL = URL;
    }

    public String getURL() {
        return URL;
    }
}
