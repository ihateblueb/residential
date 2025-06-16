package me.blueb.residential

import java.sql.Connection
import java.sql.DriverManager

class ResidentialDatabase {
    companion object {
        fun connect() {
            Class.forName("org.sqlite.JDBC")
            connection = DriverManager.getConnection("jdbc:sqlite:${Residential.instance.dataPath}/database.db")
        }

        fun setup() {
            connection.createStatement().use { stmt ->
                stmt.execute("CREATE TABLE IF NOT EXISTS resident(" +
                        "uuid uuid primary key," +
                        "claims uuid[] NULL," +
                        "trusted uuid[] NULL" +
                        ");")

                stmt.execute("CREATE TABLE IF NOT EXISTS plot(" +
                        "uuid uuid primary key," +
                        "owner uuid NULL," +
                        "price int default 0," +
                        "forSale boolean default false," +
                        "trusted uuid[] NULL" +
                        ");")

                stmt.execute("CREATE TABLE IF NOT EXISTS town(" +
                        "uuid uuid primary key," +
                        "residents uuid[] NULL," +
                        "tag varchar(16) NULL" +
                        ");")

                stmt.execute("CREATE TABLE IF NOT EXISTS nation(" +
                        "uuid uuid primary key," +
                        "towns uuid[] NULL," +
                        "tag varchar(16) NULL" +
                        ");")
            }
        }

        lateinit var connection: Connection
    }
}