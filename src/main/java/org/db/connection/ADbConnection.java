package org.db.connection;

import org.db.interfaces.IDbConnection;

public abstract class ADbConnection implements IDbConnection {
    @Override
    public Boolean connect(){
        return false;
    };
    @Override
    public Boolean check() {
        return false;
    }

}
