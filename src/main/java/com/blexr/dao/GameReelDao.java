package com.blexr.dao;

import java.util.List;

import com.blexr.entity.GameReel;

public interface GameReelDao {
    public boolean insert(GameReel gameReel);
    public List<GameReel> getAll();
}
