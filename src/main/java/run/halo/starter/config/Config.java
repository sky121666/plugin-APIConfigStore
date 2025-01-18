package run.halo.starter.config;  // 请替换为你的插件包名

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import run.halo.app.extension.AbstractExtension;
import run.halo.app.extension.GVK;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@GVK(group = "api-configstore.halo.run",            //  替换你的插件group，保持和前端的一致
    version = "v1alpha1",                  //   API 版本保持和前端一致
    kind = "Config",                     //  自定义模型类型名保持和前端一致
    plural = "configs",                //  自定义模型的复数，对应请求路径
    singular = "config")                 //  自定义模型的单数
public class Config extends AbstractExtension {
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private Spec spec;

    @Data
    @Schema(name = "ConfigSpec")//自定义`Spec` 结构名
    public static class Spec {
        @Schema(description = "备注", requiredMode = Schema.RequiredMode.REQUIRED)
        private String remark;

        @Schema(description = "标识", requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 50)
        private String identifier;

        @Schema(description = "API 地址", requiredMode = Schema.RequiredMode.REQUIRED, format = "url")
        private String apiAddress;

        @Schema(description = "JSON数据", requiredMode = Schema.RequiredMode.REQUIRED)
        private String apiData;
    }
}