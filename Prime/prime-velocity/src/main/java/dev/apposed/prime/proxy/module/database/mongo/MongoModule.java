package dev.apposed.prime.proxy.module.database.mongo;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import dev.apposed.prime.proxy.module.Module;
import lombok.Getter;
import ninja.leaping.configurate.ConfigurationNode;

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
        ConfigurationNode mongoConfig = getPlugin().getConfig().getNode("mongo");
        ConfigurationNode mongoAuthConfig = mongoConfig.getNode("auth");
        this.host = mongoConfig.getNode("host").getString();
        this.port = mongoConfig.getNode("port").getInt();
        this.database = mongoConfig.getNode("database").getString();
        this.auth = mongoConfig.getNode("enabled").getNode("enabled").getBoolean();
        this.username = mongoAuthConfig.getNode("username").getString();
        this.password = mongoAuthConfig.getNode("password").getString();
        this.authdb = mongoAuthConfig.getNode("auth-db").getString();

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