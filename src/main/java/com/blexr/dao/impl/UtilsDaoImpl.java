package com.blexr.dao.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import com.blexr.dao.utilsDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UtilsDaoImpl implements utilsDao {
    
    private static final Logger logger = LoggerFactory.getLogger(UtilsDaoImpl.class);
    
    @Autowired
    JdbcTemplate jdbcTemplate;
    
    @Override
    public boolean isInitialAppStart() {
	String sqlCommand = "SELECT  (" + 
		"(SELECT COUNT(GAME_ID) FROM game_platform) + " + 
		"(SELECT COUNT(GAME_ID) FROM game_reel) + " + 
		"(SELECT COUNT(GAME_ID) FROM game_brand) + " + 
		"(SELECT COUNT(GAME_ID) FROM game_jurisdiction) + " + 
		"(SELECT COUNT(GAME_ID) FROM game_type)  ) sum";
	try {
	    return (jdbcTemplate.queryForObject(sqlCommand, Integer.class)==0);
	}
	catch (Exception e) {
	    logger.error("isInitialAppStart", e);
	}
	return false;
    }

}
