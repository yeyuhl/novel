package io.github.yeyuhl.novel.dto.req;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * 作家注册请求DTO
 *
 * @author yeyuhl
 * @date 2023/5/6
 */
@Data
public class AuthorRegisterReqDto {

    @Schema(hidden = true)
    private Long userId;

    /**
     * 笔名
     */
    @Schema(description = "笔名")
    @Parameter(required = true)
    @NotBlank(message = "笔名不能为空！")
    private String penName;

    /**
     * 手机号码
     */
    @Schema(description = "手机号码")
    @Parameter(required = true)
    @NotBlank(message = "手机号不能为空！")
    @Pattern(regexp = "^1[3|4|5|6|7|8|9][0-9]{9}$", message = "手机号格式不正确！")
    private String telPhone;

    /**
     * QQ或微信账号
     */
    @Schema(description = "QQ或微信账号")
    @Parameter(required = true)
    @NotBlank(message = "QQ或微信账号不能为空！")
    private String chatAccount;

    /**
     * 电子邮箱
     */
    @Schema(description = "电子邮箱")
    @Parameter(required = true)
    @NotBlank(message = "电子邮箱不能为空！")
    @Email(message = "邮箱格式不正确！")
    private String email;

    /**
     * 作品方向;0-男频 1-女频
     */
    @Schema(description = "作品方向;0-男频 1-女频")
    @Parameter(required = true)
    @NotNull(message = "作品方向不能为空！")
    @Min(0)
    @Max(1)
    private Integer workDirection;

}
