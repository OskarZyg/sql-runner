package com.oskarsmc.oskar.sqlrunner.logic;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.pool.HikariPool;

public class DatabaseManager {
    private HikariPool hikariPool;

    public DatabaseManager() {
    }

    public void connect(HikariConfig hikariConfig) {
        disconnect();
        hikariPool = new HikariPool(hikariConfig);
    }

    public void disconnect() {
        if (hikariPool != null) {
            try {
                hikariPool.shutdown();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        hikariPool = null;
    }


    public HikariPool hikariPool() {
        return hikariPool;
    }

    public boolean connected() {
        if (hikariPool != null) {
            return hikariPool.poolState == HikariPool.POOL_NORMAL;
        }
        return false;
    }
}
