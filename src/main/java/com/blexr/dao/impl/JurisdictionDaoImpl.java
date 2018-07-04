package com.blexr.dao.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import com.blexr.dao.JurisdictionDao;
import com.blexr.entity.Jurisdiction;

public class JurisdictionDaoImpl implements JurisdictionDao {
    
    private static final Logger logger = LoggerFactory.getLogger(JurisdictionDaoImpl.class);
    
    private String TABLE_NAME = "jurisdiction";
    
    private String COL_ID = "ID";
    private String COL_NAME = "NAME";

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public boolean insertBatch(List<Jurisdiction> jurisdictions) {
	if (jurisdictions==null || jurisdictions.size()==0) {
	    return false;
	}

	String cqlCommand = "INSERT IGNORE INTO "+TABLE_NAME+" ("+COL_NAME+") VALUES (?)";
	try {
	    jdbcTemplate.batchUpdate(cqlCommand, new BatchPreparedStatementSetter() {
		@Override
		public void setValues(PreparedStatement ps, int i) throws SQLException {
		    int j=1;
		    ps.setString(j++, jurisdictions.get(i).getName());
		}
		        
		@Override
		public int getBatchSize() {
		    return jurisdictions.size();
		}
	    });
	}
	catch (Exception e) {
	    logger.error("insertBatch jurisdictions", e);
	    return false;
	}
	return true;
    }

    @Override
    public Integer insert(Jurisdiction jurisdiction) {
	if (jurisdiction==null) return null;
	
	try {
	    Map<String, Object> parameters = new HashMap<String, Object>();
	    parameters.put(COL_NAME, jurisdiction.getName());

	    SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate.getDataSource());

	    simpleJdbcInsert.setTableName(TABLE_NAME);
	    simpleJdbcInsert.setGeneratedKeyName(COL_ID);

	    Number insertedId = simpleJdbcInsert.executeAndReturnKey(parameters);
	    return insertedId.intValue();
	} catch (Exception e) {
	    logger.error("Exception insert jurisdiction", e);
	    return null;
	}
    }

    @Override
    public List<Jurisdiction> getAll() {
	String sqlCommand = "SELECT * FROM "+TABLE_NAME;
	try {
	    List<Jurisdiction> jurisdictions = jdbcTemplate.query(sqlCommand, new BeanPropertyRowMapper<Jurisdiction>(Jurisdiction.class));
	    return jurisdictions;
	}
	catch (Exception e) {
	    logger.error("getAll", e);
	}
	return null;
    }

}
