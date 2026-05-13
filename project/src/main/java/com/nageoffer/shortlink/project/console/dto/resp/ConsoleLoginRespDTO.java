package com.nageoffer.shortlink.project.console.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConsoleLoginRespDTO {

    private String username;

    private String realName;

    private String role;

    private String token;
}
