
package com.auction.storage.enums;

public enum ImageCategory {

    TOURNAMENT("tournaments"),

    TEAM("teams"),

    PLAYER("players"),

    SPONSOR("sponsors");

    // ==========================================
    // IMAGE STORAGE DIRECTORY
    // ==========================================

    private final String directoryName;

    ImageCategory(String directoryName) {

        this.directoryName = directoryName;
    }

    public String getDirectoryName() {

        return directoryName;
    }
}
