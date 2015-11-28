package uk.co.drnaylor.minecraft.hammer.core.commands.enums;

import com.google.common.collect.Sets;

import java.util.Set;

public enum ReloadFlagEnum implements FlagEnum {
    RELOAD_DATABASE {
        @Override
        public Set<Character> getStrings() {
            return Sets.newHashSet('d');
        }
    }
}
