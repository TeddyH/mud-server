package mud.api.common;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Component
@Scope("singleton")
public class ConfigManager {
    InputStream inputStream;

    public String getProperty(String key)  {
        Properties properties = new Properties();
        String propFile = "application.properties";
        inputStream = getClass().getClassLoader().getResourceAsStream(propFile);

        if (inputStream != null) {
            try {
                properties.load(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return properties.getProperty(key);

    }
}
