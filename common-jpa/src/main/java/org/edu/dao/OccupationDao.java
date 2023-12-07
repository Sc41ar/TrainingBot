package org.edu.dao;

import org.edu.entity.Occupation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Date;
import java.util.List;

public interface OccupationDao extends JpaRepository<Occupation, Long>, JpaSpecificationExecutor<Occupation> {
    //    @Query(value = "SELECT * FROM occupation_table", nativeQuery = true)
//    List<Occupation> selectAll();

    List<Occupation> findByOccupationNameOrderByDate(String occupationName);

    Occupation findByOccupationNameAndDate(String occupationName, Date date);
}