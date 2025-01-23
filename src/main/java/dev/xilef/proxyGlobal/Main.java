package dev.xilef.proxyGlobal;

import lombok.Getter;
import net.md_5.bungee.api.plugin.Plugin;

public final class Main extends Plugin {

    @Getter private static Main instance;

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        getProxy().getPluginManager().registerListener(this, new EventManager());
    }

}