package com.oskarsmc.oskar.sqlrunner.util;

import cloud.commandframework.CommandManager;
import cloud.commandframework.bukkit.CloudBukkitCapabilities;
import cloud.commandframework.execution.AsynchronousCommandExecutionCoordinator;
import cloud.commandframework.paper.PaperCommandManager;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.oskarsmc.oskar.sqlrunner.command.SqlRunCommand;
import com.oskarsmc.oskar.sqlrunner.logic.DatabaseManager;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.function.Function;

public class SqlRunnerModule extends AbstractModule {
    private final DatabaseManager databaseManager;
    private final Plugin plugin;

    public SqlRunnerModule(Plugin plugin) {
        this.plugin = plugin;
        databaseManager = new DatabaseManager();
    }

    @Override
    protected void configure() {
        bind(SqlRunCommand.class).in(Singleton.class);
        bind(DatabaseManager.class).toInstance(databaseManager);
    }

    @Provides
    @Singleton
    public PaperCommandManager<CommandSender> provideCommandManager() throws Exception {
        PaperCommandManager<CommandSender> commandManager = new PaperCommandManager<>(
                plugin,
                AsynchronousCommandExecutionCoordinator.simpleCoordinator(),
                Function.identity(),
                Function.identity()
        );

        if (commandManager.hasCapability(CloudBukkitCapabilities.BRIGADIER)) {
            commandManager.registerBrigadier();
        }

        return commandManager;
    }
}