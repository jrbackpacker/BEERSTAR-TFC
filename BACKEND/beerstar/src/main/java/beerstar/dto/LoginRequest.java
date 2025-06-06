// beerstar/dto/LoginRequest.java
package beerstar.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
}