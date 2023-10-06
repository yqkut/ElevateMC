package dev.apposed.prime.proxy.module.database.mongo;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import dev.apposed.prime.proxy.module.Module;
import lombok.Getter;

import java.util.Arrays;

@Getter
public class MongoModule extends Module {

    private String host, database, username, password, authdb;
    private int port;
    private boolean auth;

    private MongoDatabase mongoDatabase;
    private MongoClient mongoClient;

    @Override
    public void onEnable() {
        this.host = getPlugin().getConfig().getString("mongo.host");
        this.port = getPlugin().getConfig().getInt("mongo.port");
        this.database = getPlugin().getConfig().getString("mongo.database");
        this.auth = getPlugin().getConfig().getBoolean("mongo.auth.enabled");
        this.username = getPlugin().getConfig().getString("mongo.auth.username");
        this.password = getPlugin().getConfig().getString("mongo.auth.password");
        this.authdb = getPlugin().getConfig().getString("mongo.auth.auth-db");

        this.connect();
    }

    private void connect() {
        if(auth) {
            char[] passArr = this.password.toCharArray();
            MongoCredential credential = MongoCredential.createCredential(
                    this.username,
                    this.database,
                    passArr
            );

            this.mongoClient = new MongoClient(new ServerAddress(host, port), Arrays.asList(credential));
        } else {
            this.mongoClient = new MongoClient(this.host, this.port);
        }

        this.mongoDatabase = this.mongoClient.getDatabase(this.database);
    }
}