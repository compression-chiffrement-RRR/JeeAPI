package com.cyphernet.api.accountFriend.service;

import com.cyphernet.api.account.model.Account;
import com.cyphernet.api.account.repository.AccountRepository;
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
    private final AccountRepository accountRepository;
    private final AccountService accountService;

    @Autowired
    public AccountFriendService(AccountFriendRepository accountFriendRepository, AccountRepository accountRepository, AccountService accountService) {
        this.accountFriendRepository = accountFriendRepository;
        this.accountRepository = accountRepository;
        this.accountService = accountService;
    }

    public List<AccountFriend> getNotPendingFriends(Account account) {
        return accountFriendRepository.findByAccountUuidOrFriendUuidAndPendingAndIgnoreAndDeleted(account.getUuid(), false, false, false);
    }

    public List<AccountFriend> getPendingFriendsRequest(Account account) {
        return accountFriendRepository.findByFriendUuidAndPendingAndIgnoreAndDeleted(account.getUuid(), true, false, false);
    }

    @Transactional
    public AccountFriend addFriend(String accountUuid, String friendUuid) {
        Account account = accountService.getAccountByUuid(accountUuid)
                .orElseThrow(() -> new AccountNotFoundException("uuid", accountUuid));
        Account friend = accountService.getAccountByUuid(friendUuid)
                .orElseThrow(() -> new AccountNotFoundException("uuid", accountUuid));

        Optional<AccountFriend> optionalAccountFriend = accountFriendRepository.findFirstByAccountUuidAndFriendUuidAndDeleted(accountUuid, friendUuid, false);

        if (optionalAccountFriend.isPresent()) {
            return optionalAccountFriend.get();
        }

        optionalAccountFriend = accountFriendRepository.findFirstByAccountUuidAndFriendUuidAndDeleted(friendUuid, accountUuid, false);

        if (optionalAccountFriend.isPresent()) {
            return optionalAccountFriend.get();
        }

        AccountFriend accountFriend = new AccountFriend()
                .setAccount(account)
                .setFriend(friend);
        return accountFriendRepository.save(accountFriend);
    }

    public Optional<AccountFriend> confirmFriend(String accountUuid, String friendUuid) {
        Optional<AccountFriend> optionalAccountFriend = accountFriendRepository.findFirstByAccountUuidAndFriendUuidAndDeleted(friendUuid, accountUuid, false);

        if (optionalAccountFriend.isEmpty()) {
            return Optional.empty();
        }

        AccountFriend accountFriend = optionalAccountFriend.get();
        accountFriend.setPending(false);

        return Optional.of(accountFriendRepository.save(accountFriend));
    }

    public Optional<AccountFriend> ignoreFriend(String accountUuid, String friendUuid) {
        Optional<AccountFriend> optionalAccountFriend = accountFriendRepository.findFirstByAccountUuidAndFriendUuidAndDeleted(friendUuid, accountUuid, false);

        if (optionalAccountFriend.isEmpty()) {
            return Optional.empty();
        }

        AccountFriend accountFriend = optionalAccountFriend.get();
        accountFriend.setIgnore(true);

        return Optional.of(accountFriendRepository.save(accountFriend));
    }

    @Transactional
    public void deleteFriend(String accountUuid, String friendUuid) {
        Optional<AccountFriend> optionalAccountFriend = accountFriendRepository.findFirstByAccountUuidAndFriendUuidAndDeleted(accountUuid, friendUuid, false);

        if (optionalAccountFriend.isEmpty()) {
            optionalAccountFriend = accountFriendRepository.findFirstByAccountUuidAndFriendUuidAndDeleted(friendUuid, accountUuid, false);
            if (optionalAccountFriend.isEmpty()) {
                return;
            }
        }

        AccountFriend accountFriend = optionalAccountFriend.get();
        accountFriend.setDeleted(true);
        accountFriendRepository.save(accountFriend);
    }
}
