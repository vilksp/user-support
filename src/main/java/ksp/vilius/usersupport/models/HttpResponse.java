package ksp.vilius.usersupport.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HttpResponse {

    private int httpStatusCode;
    private HttpStatus status;
    private String response;
    private String message;
}
