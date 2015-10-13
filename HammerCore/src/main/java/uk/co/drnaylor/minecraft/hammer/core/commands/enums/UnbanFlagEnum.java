package uk.co.drnaylor.minecraft.hammer.core.commands.enums;

import java.util.HashSet;
import java.util.Set;

public enum UnbanFlagEnum implements FlagEnum {
    ALL_SERVER {
        private final Set<Character> sc = new HashSet<>();

        @Override
        public Set<Character> getStrings() {
            if (sc.isEmpty()) {
                sc.add('a');
                sc.add('g');
            }

            return sc;
        }
    },
    PERM {
        private final Set<Character> sc = new HashSet<>();

        @Override
        public Set<Character> getStrings() {
            if (sc.isEmpty()) {
                sc.add('p');
            }

            return sc;
        }
    }
}
