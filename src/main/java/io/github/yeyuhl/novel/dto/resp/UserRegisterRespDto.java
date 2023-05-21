package io.github.yeyuhl.novel.dto.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

/**
 * 用户注册响应DTO
 *
 * @author yeyuhl
 * @date 2023/5/7
 */
@Data
@Builder
public class UserRegisterRespDto {

    @Schema(description = "用户ID")
    private Long uid;

    @Schema(description = "用户token")
    private String token;
}
