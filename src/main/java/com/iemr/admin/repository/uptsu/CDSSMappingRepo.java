package com.iemr.admin.repository.uptsu;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.iemr.admin.data.uptsu.CDSSMapping;
@Repository
public interface CDSSMappingRepo extends CrudRepository<CDSSMapping, Long>{
	
	CDSSMapping findByPsmIdAndDeleted(Integer psmId, Boolean deleted);
	
	CDSSMapping findByPsmId(Integer psmId);

}
