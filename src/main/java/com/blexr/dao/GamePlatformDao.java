package com.blexr.dao;

import java.util.List;

import com.blexr.entity.GamePlatform;

public interface GamePlatformDao {
    public boolean insert(GamePlatform gamePlatform);
    public List<GamePlatform> getAll();
}
