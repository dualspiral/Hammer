package uk.co.drnaylor.minecraft.hammer.core.commands;

import ninja.leaping.configurate.ConfigurationNode;
import uk.co.drnaylor.minecraft.hammer.core.HammerConstants;
import uk.co.drnaylor.minecraft.hammer.core.HammerCore;
import uk.co.drnaylor.minecraft.hammer.core.audit.ActionEnum;
import uk.co.drnaylor.minecraft.hammer.core.audit.AuditEntry;
import uk.co.drnaylor.minecraft.hammer.core.commands.parsers.ArgumentMap;
import uk.co.drnaylor.minecraft.hammer.core.commands.parsers.HammerPlayerParser;
import uk.co.drnaylor.minecraft.hammer.core.data.HammerPlayerInfo;
import uk.co.drnaylor.minecraft.hammer.core.exceptions.HammerException;
import uk.co.drnaylor.minecraft.hammer.core.handlers.BanHandler;
import uk.co.drnaylor.minecraft.hammer.core.handlers.DatabaseConnection;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerText;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerTextBuilder;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerTextColours;
import uk.co.drnaylor.minecraft.hammer.core.wrappers.WrappedCommandSource;

import java.text.MessageFormat;
import java.util.*;

@RunAsync
public class UpgradeToPermBanCommandCore extends CommandCore {
    public UpgradeToPermBanCommandCore(HammerCore core) {
        super(core);

        permissionNodes.add("hammer.ban.perm");
    }

    @Override
    protected List<ParserEntry> createArgumentParserList() {
        List<ParserEntry> entries = new ArrayList<>();
        entries.add(new ParserEntry("player", new HammerPlayerParser(core), false));
        return entries;
    }

    @Override
    protected boolean requiresDatabase() {
        return true;
    }

    @Override
    protected boolean executeCommand(WrappedCommandSource source, ArgumentMap arguments, DatabaseConnection conn) throws HammerException {
        // Get the argument
        HammerPlayerInfo hpi = arguments.<HammerPlayerInfo>getArgument("player").get();

        int serverid = core.getConfig().getConfig().getNode("server", "id").getInt();

        // Should be there. Now, do they have a ban?
        if (core.getDatabaseConnection().getBanHandler().upgadeToPerm(hpi.getUUID(), serverid)) {
            HammerText ht = new HammerTextBuilder().add("[Hammer] The ban for " + hpi.getName() + " has been upgraded to a permanent ban.", HammerTextColours.GREEN).build();
            source.sendMessage(ht);

            ConfigurationNode cn = core.getConfig().getConfig().getNode("audit");
            if (cn.getNode("database").getBoolean() || cn.getNode("flatfile").getBoolean()) {
                createAuditEntry(source.getUUID(), hpi.getUUID(), conn);
            }

            return true;
        }

        HammerText ht;
        if (core.getDatabaseConnection().getBanHandler().getPlayerBanForServer(hpi.getUUID(), serverid) != null) {
            ht = new HammerTextBuilder().add("[Hammer] " + hpi.getName() + " has not been banned! User /permban to ban permanently.", HammerTextColours.RED).build();
        } else {
            ht = new HammerTextBuilder().add("[Hammer] " + hpi.getName() + " has already been banned permanently.", HammerTextColours.RED).build();
        }

        source.sendMessage(ht);
        return true;
    }

    private void createAuditEntry(UUID source, UUID target, DatabaseConnection conn) {
        try {
            String playerName = getName(target, conn);
            String name;
            if (source.equals(HammerConstants.consoleUUID)) {
                name = String.format("*%s*", messageBundle.getString("hammer.console"));
            } else {
                name = getName(source, conn);
            }

            AuditEntry ae = new AuditEntry(source, target, core.getConfig().getConfig().getNode("server", "id").getInt(), new Date(), ActionEnum.BAN,
                MessageFormat.format(messageBundle.getString("hammer.audit.unban"), playerName, name));
            insertAuditEntry(ae, conn);
        } catch (HammerException e) {
            core.getWrappedServer().getLogger().warn("Could not add audit entry.");
            e.printStackTrace();
        }
    }

    @Override
    protected String commandName() {
        return "toperm";
    }
}
