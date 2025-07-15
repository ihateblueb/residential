package site.remlit.blueb.residential

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import java.sql.Connection
import java.sql.DriverManager

class Database {
    init { throw AssertionError("This class is not intended to be initialized.") }
    companion object {
        fun connect() {
            val type = Configuration.config.database
            val config = Configuration.config.dbConnection
            if (!listOf("sqlite", "postgres", "mysql").contains(type))
                throw RuntimeException("Database type '$type' is invalid.")

            fun checkConfigValues() {
                if (config == null)
                    throw RuntimeException("Database configuration is missing.")
                if (config.host.isNullOrEmpty())
                    throw RuntimeException("Database host is required.")
                if (config.port.isNullOrEmpty())
                    throw RuntimeException("Database host is required.")
                if (config.name.isNullOrEmpty())
                    throw RuntimeException("Database name is required.")
                if (config.user.isNullOrEmpty())
                    throw RuntimeException("Database user is required.")
                if (config.password.isNullOrEmpty())
                    throw RuntimeException("Database password is required.")
            }

            when (type) {
                "sqlite" -> {
                    Class.forName("org.sqlite.JDBC")
                    connection = DriverManager.getConnection("jdbc:sqlite:${Residential.instance.dataFolder.path}/database.db")
                }
                "postgres" -> {
                    checkConfigValues()
                    Class.forName("org.postgresql.Driver")

                    val hikariConfig = HikariConfig()
                    hikariConfig.jdbcUrl = "jdbc:postgresql://${config!!.host}:${config.port}/${config.name}"
                    hikariConfig.username = config.user
                    hikariConfig.password = config.password

                    try {
                        val hikariDs = HikariDataSource(hikariConfig)
                        connection = hikariDs.connection
                    } catch (e: Exception) {
                        throw RuntimeException("Failed to initialize HikariDataSource: $e")
                    }
                }
                "mysql" -> {
                    checkConfigValues()
                    Class.forName("com.mysql.cj.jdbc.Driver")

                    val hikariConfig = HikariConfig()
                    hikariConfig.jdbcUrl = "jdbc:mysql://${config!!.host}:${config.port}/${config.name}"
                    hikariConfig.username = config.user
                    hikariConfig.password = config.password

                    try {
                        val hikariDs = HikariDataSource(hikariConfig)
                        connection = hikariDs.connection
                    } catch (e: Exception) {
                        throw RuntimeException("Failed to initialize HikariDataSource: $e")
                    }
                }
            }
        }


        fun setup() {
            val dbType = Configuration.config.database
            val isMySQL = (dbType == "mysql")

            /*
            * mysql sucks ass and doesn't support uuid, technically sqlite doesn't either, but it doesn't mind it being set as the type.
            * postgres runs better with it, so when dbType isn't mysql, this will set the column type to uuid instead of a varchar
            * */
            fun uuidOrVarchar() = if (isMySQL) "varchar(36)" else "uuid"

            connection.createStatement().use { stmt ->
                stmt.execute("CREATE TABLE IF NOT EXISTS database_meta(id varchar(1) primary key default 'r', version int default 0)")

                val version: Int = stmt.executeQuery("SELECT version FROM database_meta WHERE id = 'r'").use { rs ->
                    if (!rs.isBeforeFirst) {
                        stmt.execute("INSERT INTO database_meta (id, version) VALUES ('r', 0)")
                        0
                    } else {
                        if (isMySQL) rs.next()
                        rs.getInt("version")
                    }
                }

                Residential.instance.logger.info("Current database schema version is $version")

                if (version <= 0) {
                    stmt.execute("CREATE TABLE resident(uuid ${uuidOrVarchar()} primary key)")

                    stmt.execute("ALTER TABLE resident ADD COLUMN claims text NULL")
                    stmt.execute("ALTER TABLE resident ADD COLUMN trusted text NULL")

                    stmt.execute("CREATE TABLE plot(uuid ${uuidOrVarchar()} primary key)")

                    stmt.execute("ALTER TABLE plot ADD COLUMN owner ${uuidOrVarchar()} NULL")
                    stmt.execute("ALTER TABLE plot ADD COLUMN price int default 0")
                    stmt.execute("ALTER TABLE plot ADD COLUMN forSale boolean default false")
                    stmt.execute("ALTER TABLE plot ADD COLUMN trusted text NULL")
                    stmt.execute("ALTER TABLE plot ADD COLUMN chunks text NULL")

                    stmt.execute("CREATE TABLE town(uuid ${uuidOrVarchar()} primary key);")

                    stmt.execute("ALTER TABLE town ADD COLUMN tag varchar(16) NULL")
                    stmt.execute("ALTER TABLE town ADD COLUMN residents text NULL")
                    stmt.execute("ALTER TABLE town ADD COLUMN chunks text NULL")

                    stmt.execute("CREATE TABLE nation(uuid ${uuidOrVarchar()} primary key)")

                    stmt.execute("ALTER TABLE nation ADD COLUMN tag varchar(16) NULL")
                    stmt.execute("ALTER TABLE nation ADD COLUMN towns text NULL")

                    stmt.execute("UPDATE database_meta SET version = 1 WHERE id = 'r'")
                    Residential.instance.logger.info("Updated database schema to version 1")
                }

                if (version <= 1) {
                    stmt.execute("ALTER TABLE resident ADD COLUMN name varchar(16)")

                    stmt.execute("ALTER TABLE town ADD COLUMN name varchar(125)")
                    stmt.execute("ALTER TABLE town ADD COLUMN founder ${uuidOrVarchar()} NULL")
                    stmt.execute("ALTER TABLE town ADD COLUMN abandoned boolean default false")

                    stmt.execute("ALTER TABLE nation ADD COLUMN name varchar(125)")
                    stmt.execute("ALTER TABLE nation ADD COLUMN founder ${uuidOrVarchar()} NULL")

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

                    stmt.execute("ALTER TABLE chunk ADD COLUMN town ${uuidOrVarchar()} NULL")
                    stmt.execute("ALTER TABLE chunk ADD COLUMN plot ${uuidOrVarchar()} NULL")

                    stmt.execute("ALTER TABLE town DROP COLUMN chunks")
                    stmt.execute("ALTER TABLE town DROP COLUMN residents")
                    stmt.execute("ALTER TABLE plot DROP COLUMN chunks")
                    stmt.execute("ALTER TABLE resident DROP COLUMN claims")
                    stmt.execute("ALTER TABLE nation DROP COLUMN towns")

                    stmt.execute("ALTER TABLE resident ADD COLUMN town ${uuidOrVarchar()} NULL")
                    stmt.execute("ALTER TABLE town ADD COLUMN nation ${uuidOrVarchar()} NULL")
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
                    stmt.execute("CREATE TABLE town_permission(town ${uuidOrVarchar()} primary key);")

                    stmt.execute("ALTER TABLE town_permission ADD COLUMN plot ${uuidOrVarchar()} NULL")

                    // 0: any entity
                    // 1: any non-hostile entity
                    // 2: any player
                    // 3: any resident
                    stmt.execute("ALTER TABLE town_permission ADD COLUMN enter int default 1")
                    stmt.execute("ALTER TABLE town_permission ADD COLUMN break int default 3")
                    stmt.execute("ALTER TABLE town_permission ADD COLUMN place int default 3")
                    // mysql doesn't allow "use" as a column name, it's really stupid!
                    stmt.execute("ALTER TABLE town_permission ADD COLUMN ${if (isMySQL) "`use`" else "use"} int default 3")

                    stmt.execute("ALTER TABLE town_permission ADD COLUMN cmd_spawn int default 2") // t spawn

                    stmt.execute("CREATE TABLE town_role(uuid ${uuidOrVarchar()} primary key);")

                    stmt.execute("ALTER TABLE town_role ADD COLUMN name varchar(32)")

                    stmt.execute("ALTER TABLE town_role ADD COLUMN town ${uuidOrVarchar()}")

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
                    stmt.execute("ALTER TABLE town ADD COLUMN mayor ${uuidOrVarchar()} NULL")
                    stmt.execute("ALTER TABLE resident ADD COLUMN roles text NULL")

                    stmt.execute("UPDATE database_meta SET version = 7 WHERE id = 'r'")
                    Residential.instance.logger.info("Updated database schema to version 7")
                }

                if (version <= 7) {
                    stmt.execute("ALTER TABLE town ADD COLUMN abandonedAt varchar(125) NULL")

                    stmt.execute("ALTER TABLE town ADD COLUMN tax int default 0")
                    stmt.execute("ALTER TABLE town ADD COLUMN taxInterval int default 0")
                    stmt.execute("ALTER TABLE town ADD COLUMN taxPercent boolean default false")

                    stmt.execute("ALTER TABLE nation ADD COLUMN tax int default 0")
                    stmt.execute("ALTER TABLE nation ADD COLUMN taxInterval int default 0")
                    stmt.execute("ALTER TABLE nation ADD COLUMN taxPercent boolean default false")

                    stmt.execute("UPDATE database_meta SET version = 8 WHERE id = 'r'")
                    Residential.instance.logger.info("Updated database schema to version 8")
                }

                if (version <= 8) {
                    stmt.execute("ALTER TABLE chunk DROP COLUMN world")
                    stmt.execute("ALTER TABLE chunk DROP COLUMN location")
                    /*
                    * TODO: sqlite fails:
                    *  [SQLITE_ERROR] SQL error or missing database (cannot drop PRIMARY KEY column: "location")
                    * */

                    stmt.execute("ALTER TABLE chunk ADD COLUMN location varchar(256)")

                    stmt.execute("UPDATE database_meta SET version = 9 WHERE id = 'r'")
                    Residential.instance.logger.info("Updated database schema to version 9")
                }

                if (version <= 9) {
                    stmt.execute("ALTER TABLE town ADD COLUMN open boolean default false")

                    stmt.execute("UPDATE database_meta SET version = 10 WHERE id = 'r'")
                    Residential.instance.logger.info("Updated database schema to version 10")
                }

                if (version <= 10) {
                    stmt.execute("ALTER TABLE town ADD COLUMN pvp boolean default false")
                    stmt.execute("ALTER TABLE town ADD COLUMN mobs boolean default false")
                    stmt.execute("ALTER TABLE town ADD COLUMN fire boolean default false")

                    stmt.execute("ALTER TABLE town ADD COLUMN balance double precision default 0.0")

                    stmt.execute("UPDATE database_meta SET version = 11 WHERE id = 'r'")
                    Residential.instance.logger.info("Updated database schema to version 11")
                }

                if (version <= 11) {
                    stmt.execute("CREATE TABLE clock (id varchar(1) primary key);")
                    stmt.execute("ALTER TABLE clock ADD COLUMN state int default 0")

                    stmt.execute("INSERT INTO clock (id, state) VALUES ('r', 0)")

                    stmt.execute("UPDATE database_meta SET version = 12 WHERE id = 'r'")
                    Residential.instance.logger.info("Updated database schema to version 12")
                }

                if (version <= 12) {
                    stmt.execute("ALTER TABLE town DROP COLUMN tax")
                    stmt.execute("ALTER TABLE town DROP COLUMN taxInterval")

                    stmt.execute("ALTER TABLE town ADD COLUMN tax double precision null default null")
                    stmt.execute("ALTER TABLE town ADD COLUMN taxDebt boolean null default null")
                    stmt.execute("ALTER TABLE town ADD COLUMN taxFeeMultiplier double precision null default null")
                    stmt.execute("ALTER TABLE town ADD COLUMN taxMaxLate int null default null")

                    stmt.execute("UPDATE database_meta SET version = 13 WHERE id = 'r'")
                    Residential.instance.logger.info("Updated database schema to version 13")
                }
            }
        }

        lateinit var connection: Connection
        val connectionInitialized: Boolean = ::connection.isInitialized
    }
}