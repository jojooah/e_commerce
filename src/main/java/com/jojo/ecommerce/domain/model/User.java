package com.jojo.ecommerce.domain.model;

import com.jojo.ecommerce.domain.Common;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User extends Common {
    private Long userId;
    private String userName;

}
