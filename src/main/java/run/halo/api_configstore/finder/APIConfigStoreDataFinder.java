package run.halo.api_configstore.finder;


import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface APIConfigStoreDataFinder {

    Mono<APIConfigStoreDataVo> get(String dataName);

    Flux<APIConfigStoreDataVo> listAll();


}