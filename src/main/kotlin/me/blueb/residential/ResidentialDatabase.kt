package me.blueb.residential

import java.sql.Connection
import java.sql.DriverManager

class ResidentialDatabase {
    init { throw AssertionError("This class is not intended to be initialized.") }
    companion object {
        fun connect() {
            Class.forName("org.sqlite.JDBC")
            connection = DriverManager.getConnection("jdbc:sqlite:${Residential.instance.dataPath}/database.db")
        }

        fun setup() {
            connection.createStatement().use { stmt ->
                stmt.execute("CREATE TABLE IF NOT EXISTS database_meta(id varchar(1) primary key default 'r', version int default 0)")

                val version = stmt.executeQuery("SELECT version FROM database_meta WHERE id = 'r'").use { rs ->
                    if (!rs.isBeforeFirst) {
                        stmt.execute("INSERT INTO database_meta DEFAULT VALUES")
                        0
                    } else rs.getInt("version")
                }

                Residential.instance.logger.info("Current database schema version is $version")

                if (version == 0) {
                    stmt.execute("CREATE TABLE resident(uuid uuid primary key)")

                    stmt.execute("ALTER TABLE resident ADD COLUMN claims json NULL")
                    stmt.execute("ALTER TABLE resident ADD COLUMN trusted json NULL")

                    stmt.execute("CREATE TABLE plot(uuid uuid primary key)")

                    stmt.execute("ALTER TABLE plot ADD COLUMN owner uuid NULL")
                    stmt.execute("ALTER TABLE plot ADD COLUMN price int default 0")
                    stmt.execute("ALTER TABLE plot ADD COLUMN forSale boolean default false")
                    stmt.execute("ALTER TABLE plot ADD COLUMN trusted json NULL")
                    stmt.execute("ALTER TABLE plot ADD COLUMN chunks json NULL")

                    stmt.execute("CREATE TABLE town(uuid uuid primary key);")

                    stmt.execute("ALTER TABLE town ADD COLUMN tag varchar(16) NULL")
                    stmt.execute("ALTER TABLE town ADD COLUMN residents json NULL")
                    stmt.execute("ALTER TABLE town ADD COLUMN chunks json NULL")

                    stmt.execute("CREATE TABLE nation(uuid uuid primary key)")

                    stmt.execute("ALTER TABLE nation ADD COLUMN tag varchar(16) NULL")
                    stmt.execute("ALTER TABLE nation ADD COLUMN towns json NULL")

                    stmt.execute("UPDATE database_meta SET version = 1 WHERE id = 'r'")
                    Residential.instance.logger.info("Updated database schema to version 1")
                }

                if (version == 1) {
                    stmt.execute("ALTER TABLE resident ADD COLUMN name varchar(16)")

                    stmt.execute("ALTER TABLE town ADD COLUMN name varchar(125)")
                    stmt.execute("ALTER TABLE town ADD COLUMN founder uuid NULL")
                    stmt.execute("ALTER TABLE town ADD COLUMN abandoned boolean default false")

                    stmt.execute("ALTER TABLE nation ADD COLUMN name varchar(125)")
                    stmt.execute("ALTER TABLE nation ADD COLUMN founder uuid NULL")

                    stmt.execute("UPDATE database_meta SET version = 2 WHERE id = 'r'")
                    Residential.instance.logger.info("Updated database schema to version 2")
                }

                if (version == 2) {
                    stmt.execute("ALTER TABLE town ADD COLUMN foundedAt varchar(125)")

                    stmt.execute("ALTER TABLE nation ADD COLUMN foundedAt varchar(125)")

                    stmt.execute("UPDATE database_meta SET version = 3 WHERE id = 'r'")
                    Residential.instance.logger.info("Updated database schema to version 3")
                }

                if (version == 3) {
                    stmt.execute("CREATE TABLE chunk(location varchar(64) primary key);")

                    stmt.execute("ALTER TABLE chunk ADD COLUMN town uuid NULL")
                    stmt.execute("ALTER TABLE chunk ADD COLUMN plot uuid NULL")

                    stmt.execute("ALTER TABLE town DROP COLUMN chunks")
                    stmt.execute("ALTER TABLE town DROP COLUMN residents")
                    stmt.execute("ALTER TABLE plot DROP COLUMN chunks")
                    stmt.execute("ALTER TABLE resident DROP COLUMN claims")
                    stmt.execute("ALTER TABLE nation DROP COLUMN towns")

                    stmt.execute("ALTER TABLE resident ADD COLUMN town uuid NULL")
                    stmt.execute("ALTER TABLE town ADD COLUMN nation uuid NULL")
                    stmt.execute("ALTER TABLE town ADD COLUMN homeChunk varchar(64) NULL")
                    stmt.execute("ALTER TABLE town ADD COLUMN spawn varchar(264) NULL")
                    stmt.execute("ALTER TABLE plot ADD COLUMN spawn varchar(264) NULL")

                    stmt.execute("UPDATE database_meta SET version = 4 WHERE id = 'r'")
                    Residential.instance.logger.info("Updated database schema to version 4")
                }

                if (version == 4) {
                    stmt.execute("ALTER TABLE chunk ADD COLUMN world varchar(64)")

                    stmt.execute("UPDATE database_meta SET version = 5 WHERE id = 'r'")
                    Residential.instance.logger.info("Updated database schema to version 5")
                }
            }
        }

        lateinit var connection: Connection
        val connectionInitialized: Boolean = ::connection.isInitialized
    }
}