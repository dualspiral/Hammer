package uk.co.drnaylor.minecraft.hammer.core.commands.enums;

import java.util.HashSet;
import java.util.Set;

public enum KickFlagEnum implements FlagEnum {
    QUIET {
        Set<Character> strings = new HashSet<>();

        @Override
        public Set<Character> getStrings() {
            if (strings.isEmpty()) {
                strings.add('q');
            }

            return strings;
        }
    },
    NOISY {
        Set<Character> strings = new HashSet<>();

        @Override
        public Set<Character> getStrings() {
            if (strings.isEmpty()) {
                strings.add('n');
            }

            return strings;
        }
    }
}
