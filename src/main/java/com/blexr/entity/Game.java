package com.blexr.entity;

import java.util.HashSet;
import java.util.Set;

public class Game {

    private Integer id = null;
    private String name = null;
    private String details = null;
    private String url = null;
    private Image image = null;
    private Set<String> platformList = new HashSet<String>();
    private Set<String> jurisdictionList = new HashSet<String>();
    private Set<String> brandList = new HashSet<String>();
    private Set<String> reelList = new HashSet<String>();
    private Set<String> gameTypeList = new HashSet<String>();
    
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDetails() {
        return details;
    }
    public void setDetails(String details) {
        this.details = details;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public Image getImage() {
        return image;
    }
    public void setImage(Image image) {
        this.image = image;
    }
    public Set<String> getPlatformList() {
        return platformList;
    }
    public void setPlatformList(Set<String> platformList) {
        this.platformList = platformList;
    }
    public Set<String> getJurisdictionList() {
        return jurisdictionList;
    }
    public void setJurisdictionList(Set<String> jurisdictionList) {
        this.jurisdictionList = jurisdictionList;
    }
    public Set<String> getBrandList() {
        return brandList;
    }
    public void setBrandList(Set<String> brandList) {
        this.brandList = brandList;
    }
    public Set<String> getReelList() {
        return reelList;
    }
    public void setReelList(Set<String> reelList) {
        this.reelList = reelList;
    }
    public Set<String> getGameTypeList() {
        return gameTypeList;
    }
    public void setGameTypeList(Set<String> gameTypeList) {
        this.gameTypeList = gameTypeList;
    }
    
    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Game)) {
            return false;
        }
        Game game = (Game) o;
        return game.name.equals(name) && game.details.equals(details);    
    }
    
    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + name.hashCode();
        result = 31 * result + details.hashCode();
        return result;
    }
    
    
}
