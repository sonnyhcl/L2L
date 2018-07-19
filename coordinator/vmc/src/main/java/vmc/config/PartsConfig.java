package vmc.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;
//
//@ConfigurationProperties(prefix="parts")
//@Data
public class PartsConfig {
    private List<String> managers = new ArrayList<String>();
    private List<String> vessels = new ArrayList<String>();
}
