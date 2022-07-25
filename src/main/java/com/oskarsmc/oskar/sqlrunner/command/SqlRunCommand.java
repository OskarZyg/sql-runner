package com.oskarsmc.oskar.sqlrunner.command;

import cloud.commandframework.Command;
import cloud.commandframework.arguments.parser.ArgumentParseResult;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.paper.PaperCommandManager;
import com.google.inject.Inject;
import com.oskarsmc.oskar.sqlrunner.logic.DatabaseManager;
import com.zaxxer.hikari.HikariConfig;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.bukkit.command.CommandSender;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;

public class SqlRunCommand {
    @Inject
    public SqlRunCommand(PaperCommandManager<CommandSender> commandManager, DatabaseManager databaseManager) {
        Command.Builder<CommandSender> builder = commandManager.commandBuilder("sql-runner")
                .permission("sql-runner.DONOTUSEINPRODUCTION.run");
        commandManager.command(builder
                .literal("connect")
                .argument(StringArgument.of("host"))
                .argument(StringArgument.of("database"))
                .argument(StringArgument.of("username"))
                .argument(StringArgument.of("password"))
                .handler(context -> {
                    HikariConfig hikariConfig = new HikariConfig();
                    hikariConfig.setJdbcUrl("jdbc:mysql://" + context.get("host") + "/" + context.get("database"));
                    hikariConfig.setUsername(context.get("username"));
                    hikariConfig.setPassword(context.get("password"));
                    databaseManager.connect(hikariConfig);
                })
        );

        commandManager.command(builder
                .literal("disconnect")
                .handler(context -> {
                    databaseManager.disconnect();
                })
        );

        commandManager.command(builder
                .literal("run")
                .argument(StringArgument.of("query"))
                .handler(context -> {
                    try {
                        Connection connection = databaseManager.hikariPool().getConnection();
                        connection.createStatement().execute(context.get("query"));
                    } catch (SQLException e) {
                        context.getSender().sendMessage("Error: " + e.getMessage());
                        throw new RuntimeException(e);
                    }
                })
        );

        commandManager.command(builder
                .literal("run-file")
                .argument(StringArgument.of("file"))
                .handler(context -> {
                    try {
                        Connection connection = databaseManager.hikariPool().getConnection();
                        connection.createStatement().execute(context.get("file"));
                        ScriptRunner runner = new ScriptRunner(connection);
                        Reader reader = new BufferedReader(new FileReader((String) context.get("file")));
                        runner.runScript(reader);
                    } catch (SQLException | FileNotFoundException e) {
                        context.getSender().sendMessage("Error: " + e.getMessage());
                        throw new RuntimeException(e);
                    }
                })
        );
    }
}
