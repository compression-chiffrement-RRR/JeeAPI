package com.cyphernet.api.accountFriend.web;

import com.cyphernet.api.account.model.Account;
import com.cyphernet.api.account.model.AccountDTO;
import com.cyphernet.api.account.service.AccountService;
import com.cyphernet.api.accountFriend.model.AccountFriend;
import com.cyphernet.api.accountFriend.model.FriendDTO;
import com.cyphernet.api.accountFriend.service.AccountFriendService;
import com.cyphernet.api.exception.AccountNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/api/friendship")
public class AccountFriendController {
    private final AccountFriendService accountFriendService;
    private final AccountService accountService;

    @Autowired
    public AccountFriendController(AccountFriendService accountFriendService, AccountService accountService) {
        this.accountFriendService = accountFriendService;
        this.accountService = accountService;
    }

    @GetMapping("/{accountUuid}")
    public ResponseEntity<List<AccountDTO>> getFriends(@PathVariable String accountUuid) {
        Account account = accountService.getAccountByUuid(accountUuid)
                .orElseThrow(() -> new AccountNotFoundException("uuid", accountUuid));
        List<AccountFriend> friends = accountFriendService.getFriends(account);
        List<AccountDTO> friendsDTO = friends.stream().map(accountFriend -> accountFriend.getFriend().toDTO()).collect(Collectors.toList());

        return ok(friendsDTO);
    }

    @PostMapping
    public ResponseEntity<AccountFriend> addFriend(@RequestBody FriendDTO friendDTO) {
        AccountFriend accountFriend = accountFriendService.addFriend(friendDTO.getAccountUuid(), friendDTO.getFriendUuid());

        return ok(accountFriend);
    }

    @PostMapping("/confirmFriend")
    public ResponseEntity<AccountFriend> confirmFriend(@RequestBody FriendDTO friendDTO) {
        AccountFriend accountFriend = accountFriendService.confirmFriend(friendDTO.getAccountUuid(), friendDTO.getFriendUuid())
                .orElseThrow(() -> new AccountNotFoundException("uuid", friendDTO.getFriendUuid()));

        return ok(accountFriend);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteFriend(@RequestBody FriendDTO friendDTO) {
        accountFriendService.deleteFriend(friendDTO.getAccountUuid(), friendDTO.getFriendUuid());

        return ok().build();
    }
}
