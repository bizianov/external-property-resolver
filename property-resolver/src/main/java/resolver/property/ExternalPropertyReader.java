package resolver.property;

import com.esotericsoftware.yamlbeans.YamlReader;
import com.github.wnameless.json.flattener.JsonFlattener;
import com.google.gson.Gson;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

/**
 * Created by viacheslav on 5/23/17.
 */
@Component
@ConfigurationProperties(prefix = "property.resolver")
public class ExternalPropertyReader implements PropertyReader {

    private String propertyFilePath;
    private Map<String, Object> allProperties;
    private static final String EMPTY_STRING = "";

    @PostConstruct
    public void init() throws IOException {
        Reader reader = Files.newBufferedReader(Paths.get(propertyFilePath));
        YamlReader yamlReader = new YamlReader(reader);
        Map<String, Object> allPropertiesJson = yamlReader.read(Map.class);
        allProperties = convertToFlatMap(allPropertiesJson);
    }

    @Override
    public String getExternalProperty(String propName) {
        String value = allProperties.get(propName).toString();
        return value != null ? value : EMPTY_STRING;
    }

    private static Map<String, Object> convertToFlatMap(Map<String, Object> jsonMap) {
        Gson gson = new Gson();
        String json = gson.toJson(jsonMap);
        return JsonFlattener.flattenAsMap(json);
    }

    public void setPropertyFilePath(String propertyFilePath) {
        this.propertyFilePath = propertyFilePath;
    }
}
