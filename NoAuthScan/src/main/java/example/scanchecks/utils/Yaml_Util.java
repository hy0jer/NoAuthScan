package example.scanchecks.utils;

import org.yaml.snakeyaml.Yaml;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class Yaml_Util {
    public static Object search_config(Map<String, Object> map, List<String> find_config) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            String find_key = find_config.get(0);
            if (key.equals(find_key)) {
                find_config.remove(0);
                if (find_config.isEmpty()) {
                    return value;
                }
                return search_config((Map<String, Object>) value, find_config);
            }
        }
        return null;
    }

    public static Object find_config(String file_path, List<String> findList) {
        try {
            InputStream input = new FileInputStream(file_path);
            Yaml yaml = new Yaml();

            Map<String, Object> data = yaml.load(input);

           return search_config(data, findList);
        } catch (FileNotFoundException e) {
            System.out.println("Not find");
        }
        return null;
    }
}
