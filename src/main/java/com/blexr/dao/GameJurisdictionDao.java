package com.blexr.dao;

import java.util.List;

import com.blexr.entity.GameJurisdiction;

public interface GameJurisdictionDao {
    public boolean insert(GameJurisdiction gameJurisdiction);
    public boolean insert(Integer gameId, String jurisdictionName);
    public boolean delete(Integer gameId, Integer jurisdictionId);
    public List<GameJurisdiction> getAll();
    
    public boolean insertBatch(List<GameJurisdiction> gameJurisdictionList);
}
