package com.blexr.dao;

import java.util.List;

import com.blexr.entity.Image;

public interface ImageDao {
    public Integer insert(Image image);
    public boolean delete(Integer id);
    public List<Image> getAll();
    public List<Image> getAllChecksums();
    public Image get(Integer id);
    public Image getByMd5(String md5);
}
