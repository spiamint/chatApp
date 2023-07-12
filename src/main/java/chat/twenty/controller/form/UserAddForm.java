package chat.twenty.controller.form;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
public class UserAddForm {

    @NotBlank
    @Size(max = 16)
    private String loginId;
    @NotBlank
    @Size(max = 16)
    private String username;
    @NotBlank
    @Size(min = 4, max = 16)
    private String password;


}
