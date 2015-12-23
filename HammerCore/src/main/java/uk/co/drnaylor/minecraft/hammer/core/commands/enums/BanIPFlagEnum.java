package uk.co.drnaylor.minecraft.hammer.core.commands.enums;

import com.google.common.collect.Sets;

import java.util.Set;

public enum BanIPFlagEnum implements FlagEnum {
    ALL {
        private Set<Character> characters = Sets.newHashSet('a', 'g');

        @Override
        public Set<Character> getStrings() {
            return characters;
        }
    }
}
