package org.edu.dao;

import org.edu.entity.Occupation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OccupationDao extends JpaRepository<Occupation, Long>, JpaSpecificationExecutor<Occupation> {
//    @Query(value = "SELECT * FROM occupation_table", nativeQuery = true)
//    List<Occupation> selectAll();
}