package com.mobinil.xyz.util;

import java.io.InputStream;
import java.util.Properties;

/**
 *
 * @author Brain
 */
public class PropertyLoader {

    public PropertyLoader() {
    }

    public static Properties loadProperties(String name, ClassLoader loader) {
        if (name == null) {
            throw new IllegalArgumentException("null input: name");
        }
        if (name.startsWith("/")) {
            name = name.substring(1);
        }
        if (name.endsWith(".properties")) {
            name = name.substring(0, name.length() - ".properties".length());
        }
        Properties result = null;
        InputStream in = null;
        try {
            if (loader == null) {
                loader = ClassLoader.getSystemClassLoader();
            }
            //  System.out.println("name:" + name);
            name = name.replace('.', '/');
            if (!name.endsWith(".properties")) {
                name = name.concat(".properties");
            }
            System.out.println("NAME:  " + name);
            in = loader.getResourceAsStream(name);
            if (in != null) {
                //     System.out.println("in not equal null");
                result = new Properties();
                result.load(in);
            }
        } catch (Exception e) {
            result = null;
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Throwable throwable) {
                }
            }
        }
        if (result == null) {
            throw new IllegalArgumentException("could not load [" + name + "]" + " as " + "a classloader resource");
        } else {
            return result;
        }
    }

    public static Properties loadProperties(String name) {
        return loadProperties(name, Thread.currentThread().getContextClassLoader());
    }
}
