Hammer - A Ban Management system for Bukkit and Sponge servers.
===============

![build status](https://travis-ci.org/dualspiral/Hammer.svg)

Hammer is an experimental ban management plugin designed for management of single servers and server networks alike.

In development for Bukkit 1.7, Spigot 1.8+ and Sponge API servers.

### Quick guide

Hammer is being designed for use with MySQL for both single servers and server networks. Other storage mechanisms will be considered should there be a desire for it.

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

`/hammer reload` reloads the config file. Requires `hammer.admin.reload`.

#### Other permissions

`hammer.notify` - Notify this user of all bans and unbans regardless of whether -q is specified.

#### Config file notes

The `mysql` section of the config should be self explanatory.

`notifyAllOnBan` determines whether the whole server is informed of a server ban by default, or not. Use the `-q` or `-n` flags to override this behaviour.

`pollBans.enable` determines whether Hammer should poll the database periodically for new global bans for online player.
`pollBans.period` determines, in seconds, how often the task polls.

Currently, in the server section of the config, there are two options:

`server.id` - the ID of the server. Can be any positive number. Servers sharing this ID and database will share bans.

`server.name` - the friendly name of the server. Will change the friendly name in the database to this on server startup, and displayed in `/checkban` commands.

#### A note on bans.

Bans in Hammer will also be stored on the vanilla server manager, so in case of uninstall or failure, bans will remain intact (though not yet on Sponge - waiting for the `BanService` to be implemented!)

#### Jenkins builds

http://jenkins.drnaylor.co.uk/job/Hammer/

Note that any builds are provided without any warranty. You use them at your own risk.

Use HammerBukkit for 1.7.10, HammerSpigot for 1.8.* Spigot servers, HammerSponge for Sponge.

#### Contributions

I welcome contributions from anyone wishing to contribute - though note while this is still in fairly early stages (though working) things may change.
