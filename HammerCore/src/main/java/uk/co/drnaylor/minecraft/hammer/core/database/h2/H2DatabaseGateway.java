package uk.co.drnaylor.minecraft.hammer.core.database.h2;

import uk.co.drnaylor.minecraft.hammer.core.database.CommonDatabaseGateway;

import java.sql.Connection;

final class H2DatabaseGateway extends CommonDatabaseGateway {
    H2DatabaseGateway(Connection connection) {
        super(connection);
    }
}
