package com.cyphernet.api.storage.repository;

import com.cyphernet.api.storage.model.UserFileProcess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserFileProcessRepository extends JpaRepository<UserFileProcess, String> {
}
