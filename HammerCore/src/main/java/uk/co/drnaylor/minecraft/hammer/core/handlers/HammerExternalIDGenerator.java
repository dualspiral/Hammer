package uk.co.drnaylor.minecraft.hammer.core.handlers;

import java.math.BigInteger;
import java.security.SecureRandom;
import uk.co.drnaylor.minecraft.hammer.core.exceptions.HammerException;

public class HammerExternalIDGenerator {
    private final DatabaseConnection core;
    private final SecureRandom random = new SecureRandom();

    HammerExternalIDGenerator(DatabaseConnection core) {
        this.core = core;
    }

    /**
     * Returns a random 8 character ID that has not been used in the DB.
     * @return The generated ID.
     * @throws uk.co.drnaylor.minecraft.hammer.core.exceptions.HammerException
     */
    public String generateExternalId() throws HammerException {
        String s;
        do {
            s = new BigInteger(70, random).toString(36);

            // If the string is too small, or already used, get another one.
        } while (s.length() < 8 || core.getBanHandler().isExternalIdUsed(s));

        return s.substring(0, 8);
    }
}
