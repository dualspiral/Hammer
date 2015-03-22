Hammer
===============

Hammer is a ban management plugin designed for management of single servers and server networks alike.

In development for Bukkit 1.7, Spigot 1.8 and Sponge API v1.1

### Quick guide

Hammer is being designed for use with MySQL for both single servers and server networks. Other storage mechanisms will be considered should there be a desire for it.

#### Commands

`/ban [-q] [-a] <name> <reason>` - Bans a user. Requires the permissions `hammer.ban.normal`. Add `-q` to make it a quiet ban (not broadcast to everyone, if enabled in the config file), add `-a` to make this an all connected server ban (if you have `hammer.ban.all` permission.

`/gban [-q] <name> <reason>` - Bans a user on all servers. Alias for `/ban -a <name> <reason>`. Requires the permission `hammer.ban.all`.

`/permban [-q] [-a] <name> <reason>` - Permanently bans a user. Optionally on all servers. Requires the permission `hammer.ban.perm`

`/tempban [-q] <name> <time> <reason>` - Temporarily bans a user. Requires the permissions `hammer.ban.temp`. Reason is optional. Time is of format (<number>[d|h|m])

`/unban [-a] [-p] <name>` - Unbans a user from the server. -a or -p is needed if the user is all server or perm banned. Requires `hammer.unban.normal`, `hammer.unban.all` for all server bans and `hammer.unban.perm` for perm bans.

`/checkban <name>` - Checks a user's ban status.

#### Other permissions

`hammer.notify` - Notify this user of all bans and unbans regardless of whether -q is specified.

#### Config file notes

Currently, in the server section of the config, there are two options:

`server.id` - the ID of the server. Can be any positive number. Servers sharing this ID and database will share bans.

`server.name` - the friendly name of the server. Will change the friendly name in the database to this on server startup, but not currently used.

#### A note on bans.

Bans in Hammer will also be stored on the vanilla server manager, so in case of uninstall or failure, bans will remain intact.

#### Contributions

I welcome contributions from anyone wishing to contribute - though note while this is still in fairly early stages (though working on Bukkkit) things may changes.