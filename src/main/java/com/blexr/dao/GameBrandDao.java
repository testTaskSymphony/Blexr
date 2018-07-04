package com.blexr.dao;

import java.util.List;

import com.blexr.entity.GameBrand;

public interface GameBrandDao {

    public boolean insert(GameBrand gameBrand);
    public List<GameBrand> getAll();
}
