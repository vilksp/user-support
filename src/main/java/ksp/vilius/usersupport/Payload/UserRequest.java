package ksp.vilius.usersupport.Payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {

    String firstname;
    String lastname;
    String username;
    String email;
    String role;
    boolean isNonLocked;
    boolean isActive;
    MultipartFile profileImage;

}
