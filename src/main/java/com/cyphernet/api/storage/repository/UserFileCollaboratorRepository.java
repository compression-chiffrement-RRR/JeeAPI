package com.cyphernet.api.storage.repository;

import com.cyphernet.api.storage.model.UserFileCollaborator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserFileCollaboratorRepository extends JpaRepository<UserFileCollaborator, String> {
    List<UserFileCollaborator> findAllByConfirmationCollaboratorToken_Uuid(String tokenUuid);
}
