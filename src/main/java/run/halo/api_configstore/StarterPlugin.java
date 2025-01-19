package run.halo.api_configstore;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import run.halo.app.extension.SchemeManager;
import run.halo.app.plugin.BasePlugin;
import run.halo.app.plugin.PluginContext;
import run.halo.api_configstore.config.Config;

@Component
public class StarterPlugin extends BasePlugin {
    @Autowired
    private SchemeManager schemeManager;

    public StarterPlugin(PluginContext pluginContext) {
        super(pluginContext);
    }

    @Override
    public void start() {
        System.out.println("插件启动成功！");
        schemeManager.register(Config.class);
    }

    @Override
    public void stop() {
        System.out.println("插件停止！");
    }
}