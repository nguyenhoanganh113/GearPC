package com.gearpc.media.repository;

import com.gearpc.media.entity.StoredFile;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface StoredFileRepository extends JpaRepository<StoredFile, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT f FROM StoredFile f WHERE f.id = :id")
    Optional<StoredFile> findForUpdate(@Param("id") UUID id);
}
