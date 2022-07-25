package com.logger.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.logger.entity.Event;

@Repository
public interface LogEventRepository extends JpaRepository<Event, Long>{

}
