package com.blexr.dao.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import com.blexr.dao.GameReelDao;
import com.blexr.entity.GameReel;

public class GameReelDaoImpl implements GameReelDao {
    
    private static final Logger logger = LoggerFactory.getLogger(GameReelDaoImpl.class);
    
    private String TABLE_NAME = "game_reel";
    
    private String COL_GAME_ID = "GAME_ID";
    private String COL_REEL = "REEL";
    
    @Autowired
    JdbcTemplate jdbcTemplate;
    
    @Override
    public boolean insert(GameReel gameReel) {
	if (gameReel==null) {
	    return false;
	}
	String sqlCommand = "INSERT IGNORE INTO "+TABLE_NAME+" ("+COL_GAME_ID+", "+COL_REEL+") VALUES (?,?) ";
	try {
	    int rows = jdbcTemplate.update(sqlCommand, new Object[] {gameReel.getGameId(), gameReel.getReel()});
	    return rows >0;
	}catch (Exception e) {
	    logger.error("insert", e);
	}
	return false;   
    }

    @Override
    public List<GameReel> getAll() {
	String sqlCommand = "SELECT * FROM "+TABLE_NAME;
	try {
	    List<GameReel> gameReelList = jdbcTemplate.query(sqlCommand, new BeanPropertyRowMapper<GameReel>(GameReel.class));
	    return gameReelList;
	}
	catch (Exception e) {
	    logger.error("getAll", e);
	}
	return null;
    }

}
