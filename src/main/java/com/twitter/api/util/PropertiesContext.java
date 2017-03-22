package com.twitter.api.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Singleton.
 * Testing context that is shared by all tests. Contains all data needed for
 * testing. Responsible for obtaining this data at startup.
 * <p>
 * Created by mkhimich on 22.03.2017.
 */
public class PropertiesContext {
    public static final String USER_HOME_SYSTEM_PROPERTY = "user.home";
    public static final String PROPERTIES_SUFFIX = ".properties";
    private static final String TEST_PROPERTIES = "test";

    private Properties testMap = new Properties();
    private Properties generalMap = new Properties();

    private static PropertiesContext instance = new PropertiesContext();

    public static PropertiesContext getInstance() {
        return instance;
    }

    private PropertiesContext() {
        init();
    }

    public void clear() {
        generalMap.clear();
    }

    public void init() {
        try {
            //leaving for overriding properties from local property file.
            String userHome = System.getProperty(USER_HOME_SYSTEM_PROPERTY);

            loadProperties(testMap, TEST_PROPERTIES);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        generalMap.putAll(testMap);
    }

    private void loadProperties(Properties props, String fileName) throws IOException {
        //Leaving for customizing properties loading
        loadPropertiesFromClasspath(props, fileName);
    }

    private void loadPropertiesFromClasspath(Properties props, String fileName) throws IOException {
        System.out.println("Loading original properties for file " + fileName);
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream resourceAsStream = classLoader.getResourceAsStream(getFullFileName(fileName));

        if (resourceAsStream != null) {
            props.load(resourceAsStream);
        }
    }

    private String getFullFileName(String fileName) {
        return fileName + PROPERTIES_SUFFIX;
    }

    public String getProperty(String key) {
        return (String) generalMap.get(key);
    }

}