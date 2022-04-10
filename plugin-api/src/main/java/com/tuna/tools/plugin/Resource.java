package com.tuna.tools.plugin;

public class Resource {
    private String icon;
    private String name;
    private String path;

    public Resource() {
    }

    public Resource(String icon, String name, String path) {
        this.icon = icon;
        this.name = name;
        this.path = path;
    }

    public String getIcon() {
        return icon;
    }

    public Resource setIcon(String icon) {
        this.icon = icon;
        return this;
    }

    public String getName() {
        return name;
    }

    public Resource setName(String name) {
        this.name = name;
        return this;
    }

    public String getPath() {
        return path;
    }

    public Resource setPath(String path) {
        this.path = path;
        return this;
    }
}
