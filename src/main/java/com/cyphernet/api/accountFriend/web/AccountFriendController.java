package com.cyphernet.api.accountFriend.web;

import com.cyphernet.api.account.model.Account;
import com.cyphernet.api.account.model.AccountDTO;
import com.cyphernet.api.account.model.AccountDetail;
import com.cyphernet.api.account.service.AccountService;
import com.cyphernet.api.accountFriend.model.AccountFriend;
import com.cyphernet.api.accountFriend.model.AccountFriendDTO;
import com.cyphernet.api.accountFriend.model.FriendDTO;
import com.cyphernet.api.accountFriend.service.AccountFriendService;
import com.cyphernet.api.exception.AccessDeniedException;
import com.cyphernet.api.exception.AccountNotFoundException;
import com.cyphernet.api.exception.PendingInvitationNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.ResponseEntity.noContent;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/api/friend")
public class AccountFriendController {
    private final AccountFriendService accountFriendService;
    private final AccountService accountService;

    @Autowired
    public AccountFriendController(AccountFriendService accountFriendService, AccountService accountService) {
        this.accountFriendService = accountFriendService;
        this.accountService = accountService;
    }

    @Secured("ROLE_USER")
    @PreAuthorize("#accountUuid == authentication.principal.uuid")
    @GetMapping("/{accountUuid}")
    public ResponseEntity<List<AccountDTO>> getFriends(@PathVariable String accountUuid) {
        Account account = accountService.getAccountByUuid(accountUuid)
                .orElseThrow(() -> new AccountNotFoundException("uuid", accountUuid));
        List<AccountFriend> friends = accountFriendService.getFriends(account);
        List<AccountDTO> friendsDTO = friends.stream().map(accountFriend -> accountFriend.getFriend().toDTO()).collect(Collectors.toList());

        return ok(friendsDTO);
    }

    @Secured("ROLE_USER")
    @PostMapping
    public ResponseEntity<AccountFriendDTO> addFriend(@RequestBody FriendDTO friendDTO, @AuthenticationPrincipal AccountDetail currentAccount) {
        if (!currentAccount.getUuid().equals(friendDTO.accountUuid)) {
            throw new AccessDeniedException();
        }
        AccountFriend accountFriend = accountFriendService.addFriend(friendDTO.getAccountUuid(), friendDTO.getFriendUuid());

        AccountFriendDTO accountFriendDTO = new AccountFriendDTO()
                .setAccount(accountFriend.getAccount().toDTO())
                .setFriend(accountFriend.getFriend().toDTO())
                .setPending(accountFriend.getPending());

        return ok(accountFriendDTO);
    }

    @Secured("ROLE_USER")
    @PostMapping("/confirmFriend")
    public ResponseEntity<AccountFriendDTO> confirmFriend(@RequestBody FriendDTO friendDTO, @AuthenticationPrincipal AccountDetail currentAccount) {
        if (!currentAccount.getUuid().equals(friendDTO.accountUuid)) {
            throw new AccessDeniedException();
        }
        AccountFriend accountFriend = accountFriendService.confirmFriend(friendDTO.getAccountUuid(), friendDTO.getFriendUuid())
                .orElseThrow(() -> new PendingInvitationNotFoundException("uuid", friendDTO.getFriendUuid()));

        AccountFriendDTO accountFriendDTO = new AccountFriendDTO()
                .setAccount(accountFriend.getAccount().toDTO())
                .setFriend(accountFriend.getFriend().toDTO())
                .setPending(accountFriend.getPending());

        return ok(accountFriendDTO);
    }

    @Secured("ROLE_USER")
    @DeleteMapping
    public ResponseEntity<Void> deleteFriend(@RequestBody FriendDTO friendDTO, @AuthenticationPrincipal AccountDetail currentAccount) {
        if (!currentAccount.getUuid().equals(friendDTO.accountUuid)) {
            throw new AccessDeniedException();
        }
        accountFriendService.deleteFriend(friendDTO.getAccountUuid(), friendDTO.getFriendUuid());

        return noContent().build();
    }
}
