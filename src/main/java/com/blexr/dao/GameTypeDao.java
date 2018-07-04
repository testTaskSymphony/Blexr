package com.blexr.dao;

import java.util.List;

import com.blexr.entity.GameType;

public interface GameTypeDao {
    public boolean insert(GameType gameType);
    public List<GameType> getAll();
}
