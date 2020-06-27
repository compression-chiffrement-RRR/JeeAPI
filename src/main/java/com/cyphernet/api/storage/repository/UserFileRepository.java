package com.cyphernet.api.storage.repository;

import com.cyphernet.api.storage.model.UserFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserFileRepository extends JpaRepository<UserFile, String> {
    List<UserFile> findByAccountUuid(String accountUuid);
}
