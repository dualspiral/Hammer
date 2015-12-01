Hammer - A Ban Management system for Bukkit and Sponge servers.
===============

![build status](https://travis-ci.org/dualspiral/Hammer.svg)

Hammer is an experimental ban management plugin designed for management of single servers and server networks alike. Originally developed for the [InfinityMC network](http://infinitymc.us) as a ban system that works across networks and API implementations, Hammer has quickly grown to be a flexible ban management system that is open to Bukkit and Sponge servers alike.

In development for Bukkit 1.7, Spigot 1.8+ and Sponge API servers. Requires Java 8 to run.

Got an idea? Seen something is broken? File a GitHub issue and/or create a PR.

### Quick guide

Hammer is being designed for use with MySQL, SQLite and flat-file H2 databases for both single servers and server networks. Other storage mechanisms will be considered should there be a desire for it (or are contributed!)

#### Commands

`/ban [-q] [-a] [-n] <name> <reason>` - Bans a user. Requires the permissions `hammer.ban.normal`. Add `-q` to make it a quiet ban (not broadcast to everyone, if enabled in the config file), `-n` does the opposite and broadcasts the ban to everyone, add `-a` to make this an all connected server ban (if you have `hammer.ban.all` permission).

`/gban [-q] <name> <reason>` - Bans a user on all servers. Alias for `/ban -a <name> <reason>`. Requires the permission `hammer.ban.all`.

`/permban [-q] [-a] <name> <reason>` - Permanently bans a user. Optionally on all servers. Requires the permission `hammer.ban.perm`

`/tempban [-q] <name> <time> <reason>` - Temporarily bans a user. Requires the permissions `hammer.ban.temp`. Reason is optional. Time is of format (<number>[d|h|m])

`/unban [-a] [-p] <name>` - Unbans a user from the server. -a or -p is needed if the user is all server or perm banned. Requires `hammer.unban.normal`, `hammer.unban.all` for all server bans and `hammer.unban.perm` for perm bans.

`/checkban <name>` - Checks a user's ban status.

`/importplayer <player>` - Gets the player with the specified name from the Mojang servers and inserts their UUID into the database. Useful for banning players that haven't (yet) joined your server.

`/kick <player> [reason]` - Kicks the player with an optional reason.

`/kickall [-w] [reason]` - Kicks all players from the server (except yourself if you run this from the server) with an optional reason. Optionally add -w to enable the whitelist at the same time (requiring the permission `hammer.whitelist`).

`/updatebans` checks the database for any new bans for the players on the system, and kicks anyone who has recieved a global ban from another server on the network. This usually does not need to be done if the `pollBans` task is enabled, see below. Requires `hammer.admin.updatebans`.

`/hammer reload [-d]` reloads the config file, _optionally_ reloading the database config. Requires `hammer.admin.reload`.

#### Other permissions

`hammer.notify` - Notify this user of all bans and unbans regardless of whether -q is specified.

#### Config file notes

`database-engine` can currently be one of `sqlite`, `h2` or `mysql`. Defaults to `sqlite`. Make sure the JDBC driver is on the classpath for the engine you want to use! If you want a multi-server Hammer setup, you probably want `mysql`! 

The `mysql` section of the config should be self explanatory, and is only required if you have chosen the `mysql` database engine.

`notifyAllOnBan` determines whether the whole server is informed of a server ban by default, or not. Use the `-q` or `-n` flags to override this behaviour.

`pollBans.enable` determines whether Hammer should poll the database periodically for new global bans for online player.
`pollBans.period` determines, in seconds, how often the task polls.

`audit.database` and `audit.flatfile` determine whether Hammer will keep a log of ban/unban/kick actions. "database" will use an "audit" table in the database, "flatfile" will create a date-rotated log file in an appropriate place (`plugins/Hammer/logs` in Bukkit, `logs/Hammer` in Sponge)

`server.id` - the ID of the server. Can be any positive number. Servers sharing this ID and database will share bans.
`server.name` - the friendly name of the server. Will change the friendly name in the database to this on server startup, and displayed in `/checkban` commands.

#### A note on bans.

Bans in Hammer will also be stored on the vanilla server manager, so in case of uninstall or failure, bans will remain intact (though not yet on Sponge - waiting for the `BanService` to be implemented!)

#### Jenkins builds

http://jenkins.drnaylor.co.uk/job/Hammer/

Note that any builds are provided without any warranty. You use them at your own risk.

Use HammerBukkit for 1.7.10, HammerSpigot for 1.8.* Spigot servers, HammerSponge for Sponge.

#### Contributions

I welcome code contributions from anyone wishing to contribute. Send me an issue or a PR and we'll see what we can do! I tend to follow the standard Google codestyle.

I do not accept monetary contributions. If this has helped you, a thanks is all I need! :)

#### Third-party software, a shoutout!

Some versions of Hammer include third party dependencies shaded (with package relocations to avoid conflicts).

* zml's [Configurate](https://github.com/zml2008/configurate) is included in all Bukkit versions of the software, for sane and unified configuration management.
* Google's [Guava version 17](https://github.com/google/guava) is shaded in the Bukkit 1.7.10 version, as Bukkit 1.7 used older versions that Configurate cannot work with.
 
These projects make Hammer much easier to develop once for all APIs. Thanks to the authors for open sourcing these libraries!
