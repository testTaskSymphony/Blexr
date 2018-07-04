package com.blexr.dao.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import com.blexr.dao.GameBrandDao;
import com.blexr.entity.GameBrand;

public class GameBrandDaoImpl implements GameBrandDao {
    
    private static final Logger logger = LoggerFactory.getLogger(GameBrandDaoImpl.class);
    
    private String TABLE_NAME = "game_brand";
    
    private String COL_GAME_ID = "GAME_ID";
    private String COL_BRAND_NAME = "BRAND_NAME";
    
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public boolean insert(GameBrand gameBrand) {
	if (gameBrand==null) {
	    return false;
	}
	String sqlCommand = "INSERT INTO "+TABLE_NAME+" ("+COL_GAME_ID+", "+COL_BRAND_NAME+") VALUES (?,?) ";
	try {
	    int rows = jdbcTemplate.update(sqlCommand, new Object[] {gameBrand.getGameId(), gameBrand.getBrandName()});
	    return rows >0;
	}catch (Exception e) {
	    logger.error("insert", e);
	}
	return false;
    }

    @Override
    public List<GameBrand> getAll() {
	String sqlCommand = "SELECT * FROM "+TABLE_NAME;
	try {
	    List<GameBrand> gameBrandList = jdbcTemplate.query(sqlCommand, new BeanPropertyRowMapper<GameBrand>(GameBrand.class));
	    return gameBrandList;
	}
	catch (Exception e) {
	    logger.error("getAll", e);
	}
	return null;
    }

}
