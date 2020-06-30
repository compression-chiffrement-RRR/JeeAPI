package com.cyphernet.api.accountFriend.repository;

import com.cyphernet.api.accountFriend.model.AccountFriend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountFriendRepository extends JpaRepository<AccountFriend, Long> {
    List<AccountFriend> findByAccountUuid(String uuid);

    Optional<AccountFriend> findByAccountUuidAndFriendUuid(String accountUuid, String friendUuid);
}

