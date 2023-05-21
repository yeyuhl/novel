package io.github.yeyuhl.novel.dto.req;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 用户登录请求DTO
 *
 * @author yeyuhl
 * @date 2023/5/7
 */
@Data
public class UserLoginReqDto {

    @Schema(description = "手机号", example = "18888888888")
    @Parameter(required = true)
    @NotBlank(message = "手机号不能为空！")
    @Pattern(regexp = "^1[3|4|5|6|7|8|9][0-9]{9}$", message = "手机号格式不正确！")
    private String username;

    @Schema(description = "密码", example = "123456")
    @Parameter(required = true)
    @NotBlank(message = "密码不能为空！")
    private String password;

}
