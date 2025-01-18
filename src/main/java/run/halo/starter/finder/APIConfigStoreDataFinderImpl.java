package run.halo.starter.finder;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import run.halo.app.theme.finders.Finder;
import run.halo.starter.config.ConfigService;

@Component
@Finder("APIConfigStoreDataFinder")
public class APIConfigStoreDataFinderImpl implements APIConfigStoreDataFinder {


    private final ConfigService configService;

    @Autowired
    public APIConfigStoreDataFinderImpl(ConfigService configService) {
        this.configService = configService;
    }


    @Override
    public Mono<APIConfigStoreDataVo> get(String dataName) {
        return   configService.getByName(dataName)
            .map(config -> {
                APIConfigStoreDataVo vo = new APIConfigStoreDataVo();
                vo.setRemark(config.getSpec().getRemark());
                vo.setIdentifier(config.getSpec().getIdentifier());
                vo.setApiAddress(config.getSpec().getApiAddress());
                vo.setApiData(config.getSpec().getApiData());
                return vo;

            });
    }

    @Override
    public Flux<APIConfigStoreDataVo> listAll() {
        return configService.list()
            .map(config ->
                {
                    APIConfigStoreDataVo vo = new APIConfigStoreDataVo();
                    vo.setRemark(config.getSpec().getRemark());
                    vo.setIdentifier(config.getSpec().getIdentifier());
                    vo.setApiAddress(config.getSpec().getApiAddress());
                    vo.setApiData(config.getSpec().getApiData());
                    return vo;
                }
            );
    }
}