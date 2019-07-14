package uk.co.drnaylor.minecraft.hammer.core.config;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class HammerConfig {

    @Setting(value = "database-engine",
            comment = "The DB engine to use. Valid options are \"sqlite\", \"h2\" and \"mysql\". The required JDBC driver must be on the classpath.")
    private String engine = "sqlite";

    @Setting(value = "mysql", comment = "This section is only required if MySQL is selected for the database engine.")
    private MySql mySql = new MySql();

    @Setting(value = "server")
    private Server server = new Server();

    @Setting(value = "notifyAllOnBan", comment = "If set to false, only those with the 'hammer.notify' permission will be notified when someone is banned.")
    private boolean notifyAllOnBan = true;

    @Setting("pollBans")
    private PollBans pollBans = new PollBans();

    @Setting(value = "audit", comment = "Whether or not an audit log should be kept.")
    private Audit audit = new Audit();

    @Setting(value = "appendBanReasons", comment = "If this is true, and a ban is applied on top of a previous (lesser) ban, the previous ban reason will be appeneded to the new one.")
    private boolean appendBanReasons = false;

    @Setting(value = "redis", comment = "Redis support for realtime subscription to ban events.")
    private Redis redis = new Redis();

    public String getEngine() {
        return engine;
    }

    public MySql getMySql() {
        return mySql;
    }

    public Server getServer() {
        return server;
    }

    public boolean isNotifyAllOnBan() {
        return notifyAllOnBan;
    }

    public PollBans getPollBans() {
        return pollBans;
    }

    public Audit getAudit() {
        return audit;
    }

    public boolean isAppendBanReasons() {
        return appendBanReasons;
    }

    public Redis getRedis() {
        return redis;
    }

    @ConfigSerializable
    public static class MySql {

        @Setting(value = "host", comment = "The location of the database")
        private String host = "localhost";

        @Setting(value = "port", comment = "The port for the database")
        private int port = 3306;

        @Setting(value = "database", comment = "The name of the database to connect to.")
        private String database = "hammer";

        @Setting(value = "username", comment = "The username for the database connection")
        private String username = "username";

        @Setting(value = "password", comment = "The password for the database connection")
        private String password = "password";

        public String getHost() {
            return host;
        }

        public int getPort() {
            return port;
        }

        public String getDatabase() {
            return database;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }
    }

    @ConfigSerializable
    public static class Server {

        @Setting(value = "id", comment = "A unique integer id to represent this server")
        private int id = 1;

        @Setting(value = "name", comment = "A display name for this server when using Hammer")
        private String name = "New Server";

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }

    @ConfigSerializable
    public static class PollBans {

        @Setting(value = "enable", comment = "If set to true, Hammer will check the database periodically to see if any online player have recieved a global ban and will ban them accordingly.")
        private boolean enable = true;

        @Setting(value = "period", comment = "How often, in seconds, Hammer will check the database for new bans")
        private int period = 60;

        public boolean isEnable() {
            return enable;
        }

        public int getPeriod() {
            return period;
        }
    }

    @ConfigSerializable
    public static class Audit {

        @Setting(value = "database", comment = "Keep an audit log in the database")
        private boolean database = true;

        @Setting(value = "flatfile", comment = "Keep an audit log in a flat file")
        private boolean flatfile = false;

        public boolean isDatabase() {
            return database;
        }

        public boolean isFlatfile() {
            return flatfile;
        }

        public boolean isAuditActive() {
            return database || flatfile;
        }
    }

    @ConfigSerializable
    public static class Redis {

        @Setting(value = "enabled", comment = "Enables real-time subscription to bans across the network. Requires a Redis v3 server. "
                + "Recommended for networks")
        private boolean enable = false;

        @Setting(value = "hostname", comment = "The hostname of the Redis server")
        private String hostname = "localhost";

        @Setting(value = "port", comment = "The port the Redis server listens on. Defaults to 6379")
        private int port = 6379;

        public boolean isEnable() {
            return enable;
        }

        public String getHostname() {
            return hostname;
        }

        public int getPort() {
            return port;
        }
    }
}
