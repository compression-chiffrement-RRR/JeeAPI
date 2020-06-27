package com.cyphernet.api.accountFriend.service;

import com.cyphernet.api.account.model.Account;
import com.cyphernet.api.account.service.AccountService;
import com.cyphernet.api.accountFriend.model.AccountFriend;
import com.cyphernet.api.accountFriend.repository.AccountFriendRepository;
import com.cyphernet.api.exception.AccountNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public List<AccountFriend> getFriends(Account account) {
        return accountFriendRepository.findByAccountUuid(account.getUuid());
    }

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
        Optional<AccountFriend> optionalAccountFriend = accountFriendRepository.findByAccountUuidAndFriendUuid(accountUuid, friendUuid);

        if (optionalAccountFriend.isEmpty()) {
            return Optional.empty();
        }

        AccountFriend accountFriend = optionalAccountFriend.get();
        accountFriend.setPending(false);

        return Optional.of(accountFriendRepository.save(accountFriend));
    }
}
