package uk.co.drnaylor.minecraft.hammer.core.commands.enums;

import com.google.common.collect.Sets;

import java.util.Set;

public enum KickAllFlagEnum implements FlagEnum {
    WHITELIST {
        @Override
        public Set<Character> getStrings() {
            return Sets.newHashSet('w');
        }
    }
}
