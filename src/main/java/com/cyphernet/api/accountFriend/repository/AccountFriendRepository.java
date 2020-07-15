package com.cyphernet.api.accountFriend.repository;

import com.cyphernet.api.accountFriend.model.AccountFriend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountFriendRepository extends JpaRepository<AccountFriend, Integer> {
    @Query("FROM AccountFriend af WHERE (af.account.uuid = ?1 OR af.friend.uuid = ?1) AND af.pending = ?2 AND af.ignore = ?3 AND af.deleted = ?4")
    List<AccountFriend> findByAccountUuidOrFriendUuidAndPendingAndIgnoreAndDeleted(String uuid, Boolean pending, Boolean ignore, Boolean deleted);

    List<AccountFriend> findByFriendUuidAndPendingAndIgnoreAndDeleted(String uuid, Boolean pending, Boolean ignore, Boolean deleted);

    Optional<AccountFriend> findFirstByAccountUuidAndFriendUuidAndDeleted(String accountUuid, String friendUuid, Boolean deleted);
}

