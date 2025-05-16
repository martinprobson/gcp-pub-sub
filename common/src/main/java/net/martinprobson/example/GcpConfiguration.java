package net.martinprobson.example;

import com.google.auth.oauth2.ServiceAccountCredentials;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class GcpConfiguration {

    protected static final Config config = ConfigFactory.load();

    private static final Logger logger = LoggerFactory.getLogger(GcpConfiguration.class);

    private static ServiceAccountCredentials credentials;

    protected static synchronized ServiceAccountCredentials loadCredentials() throws IOException {
        if (credentials != null) {
            return credentials;
        }
        var file = config.getString("credentials.file");
        logger.info("Loading credentials from {}",file);

        File credentialsPath = new File(file);
        ServiceAccountCredentials serviceAccountCredentials;
        try (FileInputStream serviceAccountStream = new FileInputStream(file)) {
            serviceAccountCredentials = ServiceAccountCredentials.fromStream(serviceAccountStream);
        }
        credentials = serviceAccountCredentials;
        return credentials;
    }
}
