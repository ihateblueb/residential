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

                if (version <= 0) {
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

                if (version <= 1) {
                    stmt.execute("ALTER TABLE resident ADD COLUMN name varchar(16)")

                    stmt.execute("ALTER TABLE town ADD COLUMN name varchar(125)")
                    stmt.execute("ALTER TABLE town ADD COLUMN founder uuid NULL")
                    stmt.execute("ALTER TABLE town ADD COLUMN abandoned boolean default false")

                    stmt.execute("ALTER TABLE nation ADD COLUMN name varchar(125)")
                    stmt.execute("ALTER TABLE nation ADD COLUMN founder uuid NULL")

                    stmt.execute("UPDATE database_meta SET version = 2 WHERE id = 'r'")
                    Residential.instance.logger.info("Updated database schema to version 2")
                }

                if (version <= 2) {
                    stmt.execute("ALTER TABLE town ADD COLUMN foundedAt varchar(125)")

                    stmt.execute("ALTER TABLE nation ADD COLUMN foundedAt varchar(125)")

                    stmt.execute("UPDATE database_meta SET version = 3 WHERE id = 'r'")
                    Residential.instance.logger.info("Updated database schema to version 3")
                }

                if (version <= 3) {
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

                if (version <= 4) {
                    stmt.execute("ALTER TABLE chunk ADD COLUMN world varchar(64)")

                    stmt.execute("UPDATE database_meta SET version = 5 WHERE id = 'r'")
                    Residential.instance.logger.info("Updated database schema to version 5")
                }

                if (version <= 5) {
                    stmt.execute("CREATE TABLE town_permission(town uuid primary key);")

                    stmt.execute("ALTER TABLE town_permission ADD COLUMN plot uuid NULL")

                    // 0: any entity
                    // 1: any non-hostile entity
                    // 2: any player
                    // 3: any resident
                    stmt.execute("ALTER TABLE town_permission ADD COLUMN enter int default 1")
                    stmt.execute("ALTER TABLE town_permission ADD COLUMN break int default 3")
                    stmt.execute("ALTER TABLE town_permission ADD COLUMN place int default 3")
                    stmt.execute("ALTER TABLE town_permission ADD COLUMN use int default 3")

                    stmt.execute("ALTER TABLE town_permission ADD COLUMN cmd_spawn int default 2") // t spawn

                    stmt.execute("CREATE TABLE town_role(uuid uuid primary key);")

                    stmt.execute("ALTER TABLE town_role ADD COLUMN name varchar(32)")

                    stmt.execute("ALTER TABLE town_role ADD COLUMN town uuid")

                    stmt.execute("ALTER TABLE town_role ADD COLUMN is_default boolean default false") // will make default role every resident gets

                    stmt.execute("ALTER TABLE town_role ADD COLUMN bank_withdraw boolean default false")
                    stmt.execute("ALTER TABLE town_role ADD COLUMN bank_deposit boolean default true")

                    stmt.execute("ALTER TABLE town_role ADD COLUMN cmd_plot_management boolean default false")
                    stmt.execute("ALTER TABLE town_role ADD COLUMN cmd_mayor boolean default false")

                    stmt.execute("UPDATE database_meta SET version = 6 WHERE id = 'r'")
                    Residential.instance.logger.info("Updated database schema to version 6")
                }

                if (version <= 6) {
                    stmt.execute("ALTER TABLE town_role ADD COLUMN is_mayor boolean default false")
                    stmt.execute("ALTER TABLE town ADD COLUMN mayor uuid NULL")
                    stmt.execute("ALTER TABLE resident ADD COLUMN roles json NULL")

                    stmt.execute("UPDATE database_meta SET version = 7 WHERE id = 'r'")
                    Residential.instance.logger.info("Updated database schema to version 7")
                }
            }
        }

        lateinit var connection: Connection
        val connectionInitialized: Boolean = ::connection.isInitialized
    }
}