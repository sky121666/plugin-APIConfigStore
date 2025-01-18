package run.halo.starter.config;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;
import run.halo.app.core.extension.endpoint.CustomEndpoint;


import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Component
public class ConfigEndpoint implements CustomEndpoint {

    private final ConfigService configService;

    public ConfigEndpoint(ConfigService configService) {
        this.configService = configService;
    }

    @Override
    public RouterFunction<ServerResponse> endpoint() {
        return  route()
            .GET("/configs", accept(APPLICATION_JSON), this::listConfigs) // 返回所有的列表数据
            .GET("/configs/{name}", accept(APPLICATION_JSON), this::getConfigByName) // 根据 metadata.name 返回单个信息
            .POST("/configs", accept(APPLICATION_JSON), this::createConfig)      // 创建Config
            .PUT("/configs/{name}", accept(APPLICATION_JSON), this::updateConfig)  // 根据 metadata.name 更新Config
            .DELETE("/configs/{name}", accept(APPLICATION_JSON), this::deleteConfig) // 根据 metadata.name 删除Config
            .build();
    }

    private Mono<ServerResponse> listConfigs(ServerRequest request) {
        return configService.list().collectList()
            .flatMap(configs -> ServerResponse.ok().bodyValue(configs)) ;
    }

    private  Mono<ServerResponse> getConfigByName(ServerRequest request){
        String name = request.pathVariable("name");
        return  configService.getByName(name)
            .flatMap(config -> ServerResponse.ok().bodyValue(config));
    }

    private Mono<ServerResponse> createConfig(ServerRequest request) {
        return request.bodyToMono(Config.class)
            .flatMap(configService::create)
            .flatMap(config -> ServerResponse.status(HttpStatus.CREATED).bodyValue(config));
    }

    private Mono<ServerResponse> updateConfig(ServerRequest request) {
        String name = request.pathVariable("name");
        return request.bodyToMono(Config.class)
            .flatMap(config->{
                config.getMetadata().setName(name);
                return  configService.update(config);
            }).flatMap(config-> ServerResponse.ok().bodyValue(config));
    }
    private  Mono<ServerResponse> deleteConfig(ServerRequest request) {
        String name = request.pathVariable("name");
        return configService.delete(name)
            .then( ServerResponse.noContent().build()) ;
    }


}