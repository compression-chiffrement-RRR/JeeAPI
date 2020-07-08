package com.cyphernet.api.accountFriend.model;

import com.cyphernet.api.account.model.AccountDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class AccountFriendDTO {
    AccountDTO account;

    AccountDTO friend;

    boolean pending;
}
