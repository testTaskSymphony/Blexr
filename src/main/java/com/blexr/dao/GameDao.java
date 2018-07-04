package com.blexr.dao;

import java.util.List;

import com.blexr.entity.Game;

public interface GameDao {
    public Integer insert(Game game);
    public boolean delete(Integer id);
    public List<Game> getAll();
    
    public boolean insertBatch(List<Game> games);
}
