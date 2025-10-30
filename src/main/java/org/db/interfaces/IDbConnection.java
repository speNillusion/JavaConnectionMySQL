package org.db.interfaces;

/**
 * That Class Will connect your code on your DB MySQL
 * @version 1.0.0
 **/
public interface IDbConnection {
    /**
     * That command connect on Db, use to initiate the db
     * @return Will return true or false, if returns false use Check();
     **/
    Boolean connect();
    /**
     * That command check the tables in Db, if do not exist, he will create thens
     * @return Will return false if the commands don't run.
     **/
    Boolean check();
}
