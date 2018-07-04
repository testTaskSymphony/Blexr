package com.blexr.dao;

import java.util.List;

import com.blexr.entity.Jurisdiction;

public interface JurisdictionDao {

    public boolean insertBatch(List<Jurisdiction> jurisdictions);
    public Integer insert(Jurisdiction jurisdiction); 
    public List<Jurisdiction> getAll();
}
