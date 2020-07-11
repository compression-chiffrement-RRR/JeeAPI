package com.cyphernet.api.accountFriend.service;

import com.cyphernet.api.account.model.Account;
import com.cyphernet.api.account.service.AccountService;
import com.cyphernet.api.accountFriend.model.AccountFriend;
import com.cyphernet.api.accountFriend.repository.AccountFriendRepository;
import com.cyphernet.api.exception.AccountNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AccountFriendService {

    private final AccountFriendRepository accountFriendRepository;
    private final AccountService accountService;

    @Autowired
    public AccountFriendService(AccountFriendRepository accountFriendRepository, AccountService accountService) {
        this.accountFriendRepository = accountFriendRepository;
        this.accountService = accountService;
    }

    public List<AccountFriend> getNotPendingFriends(Account account) {
        return accountFriendRepository.findByAccountUuidAndPendingAndIgnore(account.getUuid(), false, false);
    }

    public List<AccountFriend> getPendingFriendsRequest(Account account) {
        return accountFriendRepository.findByFriendUuidAndPendingAndIgnore(account.getUuid(), true, false);
    }

    @Transactional
    public AccountFriend addFriend(String accountUuid, String friendUuid) {
        Account account = accountService.getAccountByUuid(accountUuid)
                .orElseThrow(() -> new AccountNotFoundException("uuid", accountUuid));
        Account friend = accountService.getAccountByUuid(friendUuid)
                .orElseThrow(() -> new AccountNotFoundException("uuid", accountUuid));
        AccountFriend accountFriend = new AccountFriend()
                .setAccount(account)
                .setFriend(friend);
        return accountFriendRepository.save(accountFriend);
    }

    public Optional<AccountFriend> confirmFriend(String accountUuid, String friendUuid) {
        Optional<AccountFriend> optionalAccountFriend = accountFriendRepository.findByAccountUuidAndFriendUuid(friendUuid, accountUuid);

        if (optionalAccountFriend.isEmpty()) {
            return Optional.empty();
        }

        AccountFriend accountFriend = optionalAccountFriend.get();
        accountFriend.setPending(false);

        return Optional.of(accountFriendRepository.save(accountFriend));
    }

    public Optional<AccountFriend> ignoreFriend(String accountUuid, String friendUuid) {
        Optional<AccountFriend> optionalAccountFriend = accountFriendRepository.findByAccountUuidAndFriendUuid(friendUuid, accountUuid);

        if (optionalAccountFriend.isEmpty()) {
            return Optional.empty();
        }

        AccountFriend accountFriend = optionalAccountFriend.get();
        accountFriend.setIgnore(true);

        return Optional.of(accountFriendRepository.save(accountFriend));
    }

    public void deleteFriend(String accountUuid, String friendUuid) {
        Optional<AccountFriend> optionalAccountFriend = accountFriendRepository.findByAccountUuidAndFriendUuid(accountUuid, friendUuid);

        if (optionalAccountFriend.isEmpty()) {
            optionalAccountFriend = accountFriendRepository.findByAccountUuidAndFriendUuid(friendUuid, accountUuid);
            if (optionalAccountFriend.isEmpty()) {
                return;
            }
        }

        AccountFriend accountFriend = optionalAccountFriend.get();
        accountFriendRepository.delete(accountFriend);
    }
}
