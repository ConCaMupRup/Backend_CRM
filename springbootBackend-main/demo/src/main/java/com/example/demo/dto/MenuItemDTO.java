package com.example.demo.dto;

import java.util.List;

public class MenuItemDTO {
    private String key;
    private String label;
    private String icon;
    private String path;
    private List<MenuItemDTO> children;

    public MenuItemDTO() {}

    public MenuItemDTO(String key, String label, String icon, String path) {
        this.key = key;
        this.label = label;
        this.icon = icon;
        this.path = path;
    }

    // Getters and Setters
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public List<MenuItemDTO> getChildren() {
        return children;
    }

    public void setChildren(List<MenuItemDTO> children) {
        this.children = children;
    }
} 