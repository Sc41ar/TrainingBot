package org.edu.dao;

import org.edu.entity.Occupation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OccupationDao extends JpaRepository<Occupation, Long> {
}
