package com.oskarsmc.oskar.sqlrunner;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.oskarsmc.oskar.sqlrunner.command.SqlRunCommand;
import com.oskarsmc.oskar.sqlrunner.util.SqlRunnerModule;
import org.bukkit.plugin.java.JavaPlugin;

public final class SqlRunnerPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        Injector injector = Guice.createInjector(new SqlRunnerModule(this));
        injector.getInstance(SqlRunCommand.class);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
