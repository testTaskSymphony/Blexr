package com.blexr.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import com.blexr.dao.ImageDao;
import com.blexr.entity.Image;

public class ImageDaoImpl implements ImageDao {
    
    private static final Logger logger = LoggerFactory.getLogger(ImageDaoImpl.class);

    @Autowired
    JdbcTemplate jdbcTemplate;
    
    private String TABLE_NAME = "image";
    
    private String COL_ID = "ID";
    private String COL_MD5 = "MD5";
    private String COL_FILE = "FILE";
    
    @Override
    public Integer insert(Image image) {
	if (image==null) {
	    return null;
	}
	if (image.getMd5()==null || image.getFile()==null) {
	    return null;
	}
	
	try {
	    Map<String, Object> parameters = new HashMap<String, Object>();
	    parameters.put(COL_MD5, image.getMd5());
	    parameters.put(COL_FILE, image.getFile());

	    SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate.getDataSource());

	    simpleJdbcInsert.setTableName(TABLE_NAME);
	    simpleJdbcInsert.setGeneratedKeyName("ID");

	    Number insertedId = simpleJdbcInsert.executeAndReturnKey(parameters);
	    return insertedId.intValue();
	} catch(DuplicateKeyException dke) {
	    // Image with the same md5 allready exists. Get it from db and return that id
	    Image img = getByMd5(image.getMd5());
	    return img.getId();
	}
	catch (Exception e) {
	    logger.error("Exception insert image", e);
	    return null;
	}
    }

    @Override
    public boolean delete(Integer id) {
	if (id==null) {
	    return false;
	}
	String sqlCommand = "DELETE FROM "+TABLE_NAME+ " WHERE "+COL_ID+"=?";

	try {
	    int rows = jdbcTemplate.update(sqlCommand, new Object[] {id});
	    return rows > 0;
	}
	catch (Exception e) {
	    logger.error("delete", e);
	}
	return false;
    }

    @Override
    public List<Image> getAll() {
	String select = "SELECT * FROM "+TABLE_NAME;
	try {
	    return jdbcTemplate.query(select, new BeanPropertyRowMapper<Image>(Image.class));
	}
	catch (Exception e) {
	    logger.error("getAll", e);
	}
	return null;
    }

    @Override
    public Image get(Integer id) {
	if (id==null) {
	    return null;
	}
	String select = "SELECT * FROM "+TABLE_NAME + " WHERE "+COL_ID+"=?";
	try {
	    return jdbcTemplate.queryForObject(select, new BeanPropertyRowMapper<Image>(Image.class), new Object[] {id});
	}
	catch (Exception e) {
	    logger.error("get", e);
	}
	return null;
    }

    @Override
    public List<Image> getAllChecksums() {
	String select = "SELECT " +COL_ID +","+ COL_MD5 + " FROM "+TABLE_NAME;
	try {
	    return jdbcTemplate.query(select, new BeanPropertyRowMapper<Image>(Image.class));
	}
	catch (Exception e) {
	    logger.error("getAllChecksums", e);
	}
	return null;
    }

    @Override
    public Image getByMd5(String md5) {
	if (md5==null) {
	    return null;
	}
	String select = "SELECT * FROM "+TABLE_NAME + " WHERE "+COL_MD5+"=?";
	try {
	    return jdbcTemplate.queryForObject(select, new BeanPropertyRowMapper<Image>(Image.class), new Object[] {md5});
	}
	catch (Exception e) {
	    logger.error("getByMd5", e);
	}
	return null;
    }

}
