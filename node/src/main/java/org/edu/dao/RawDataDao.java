package org.edu.dao;

import org.edu.entity.RawData;
import org.springframework.data.jpa.repository.JpaRepository;

//репозиторый для сохранения сообщений
public interface RawDataDao extends JpaRepository<RawData, Long>{

}
