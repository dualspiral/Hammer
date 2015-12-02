/*
 * This file is part of Hammer, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2015 Daniel Naylor
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package uk.co.drnaylor.minecraft.hammer.core.commands;

import com.google.common.collect.Lists;
import uk.co.drnaylor.minecraft.hammer.core.HammerCore;
import uk.co.drnaylor.minecraft.hammer.core.commands.enums.ReloadFlagEnum;
import uk.co.drnaylor.minecraft.hammer.core.commands.parsers.ArgumentMap;
import uk.co.drnaylor.minecraft.hammer.core.commands.parsers.FlagParser;
import uk.co.drnaylor.minecraft.hammer.core.exceptions.HammerException;
import uk.co.drnaylor.minecraft.hammer.core.handlers.DatabaseConnection;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerText;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerTextBuilder;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerTextColours;
import uk.co.drnaylor.minecraft.hammer.core.wrappers.WrappedCommandSource;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RunAsync
public class ReloadCommandCore extends CommandCore {

    private final HammerText txt =
            new HammerTextBuilder().add("[Hammer] Configuration reloaded.", HammerTextColours.GREEN).build();
    private final HammerText dbtxt =
            new HammerTextBuilder().add("[Hammer] Configuration & database reloaded.", HammerTextColours.GREEN).build();

    public ReloadCommandCore(HammerCore core) {
        super(core);

        this.permissionNodes.add("hammer.admin.reload");
    }

    @Override
    protected List<ParserEntry> createArgumentParserList() {
        return Lists.newArrayList(new ParserEntry("databaseReload", new FlagParser<>(ReloadFlagEnum.class), true));
    }

    @Override
    protected boolean requiresDatabase() {
        return false;
    }

    @Override
    protected boolean executeCommand(WrappedCommandSource source, ArgumentMap arguments, DatabaseConnection conn) throws HammerException {
        try {
            Optional<List<ReloadFlagEnum>> f = arguments.getArgument("databaseReload");
            boolean db = f.isPresent() && f.get().contains(ReloadFlagEnum.RELOAD_DATABASE);
            core.reloadConfig(db);
            source.sendMessage(db ? dbtxt : txt);
        } catch (IOException e) {
            e.printStackTrace();
            throw new HammerException("Reload failed.", e);
        } catch (HammerException e) {
            e.printStackTrace();
            throw new HammerException("The configuration has been reloaded, but there was an error connecting to the new database. We will continue to use the current database.", e);
        }

        return true;
    }

    @Override
    protected String commandName() {
        return "hammerreload";
    }
}
