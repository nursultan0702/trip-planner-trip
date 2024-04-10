package com.tripplannertrip.repository;

import com.tripplannertrip.entity.NotificationOutbox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationOutboxRepository extends JpaRepository<NotificationOutbox, Long> {
    @Query(
            value =
                    "SELECT * FROM notification_outbox n "
                            + "WHERE n.status IN :statuses FOR UPDATE SKIP LOCKED LIMIT 5",
            nativeQuery = true)
    List<NotificationOutbox> findByStatuses(@Param("statuses") List<String> statuses);
}
