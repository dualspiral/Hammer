package uk.co.drnaylor.minecraft.hammer.core.commands.parsers;

import java.util.HashMap;
import java.util.Optional;

public class ArgumentMap extends HashMap<String, Object> {

    @SuppressWarnings("unchecked")
    public <T> Optional<T> getArgument(String key) {
        Object value = this.get(key);
        try {
            if (value != null) {
                return Optional.of((T) value);
            }
        } catch (ClassCastException e) {}

        return Optional.empty();
    }
}
