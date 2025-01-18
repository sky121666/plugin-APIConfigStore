package run.halo.starter.config;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import run.halo.app.extension.ListOptions;
import run.halo.app.extension.ReactiveExtensionClient;

@Service
public class ConfigService {

    private final ReactiveExtensionClient extensionClient;

    public ConfigService(ReactiveExtensionClient extensionClient) {
        this.extensionClient = extensionClient;
    }

    public Mono<Config> create(Config config) {
        return extensionClient.create(config);
    }

    public Mono<Config> getByName(String name) {
        return extensionClient.fetch(Config.class, name);
    }



    public Flux<Config> list() {
        return extensionClient.listAll(Config.class, ListOptions.builder().build(), Sort.unsorted());
    }

    public Mono<Config> update(Config config) {
        return extensionClient.update(config);
    }

    public Mono<Void> delete(String name) {
        return extensionClient.fetch(Config.class, name)
            .flatMap(extensionClient::delete)
            .then();
    }
}