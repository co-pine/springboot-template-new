package com.pine.template.model.dto;

import lombok.Builder;
import lombok.Data;

/**
 * @author pine
 */
@Data
@Builder
public class UserLoginByAccountRequest {

    private String account;

    private String password;

}
