package com.db.dataplatform.techtest.server.persistence.repository;

import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;
import com.db.dataplatform.techtest.server.persistence.model.DataBodyEntity;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DataStoreRepository extends JpaRepository<DataBodyEntity, Long> {
	List<DataBodyEntity> findByDataHeaderEntityBlocktype(BlockTypeEnum blocktype);
	Optional<DataBodyEntity> findByDataHeaderEntityName(String name);
}
