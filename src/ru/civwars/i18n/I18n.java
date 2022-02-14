package ru.civwars.i18n;

import com.google.common.collect.Maps;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.regex.Pattern;
import org.bukkit.plugin.java.JavaPlugin;
import ru.lib27.annotation.NotNull;
import ru.lib27.annotation.Nullable;

public class I18n {

    private static final String MESSAGES = "messages";
    private static final Pattern NODOUBLEMARK = Pattern.compile("''");
    private static final ResourceBundle NULL_BUNDLE = new ResourceBundle() {
        public Locale getLocale() {
            return Locale.ENGLISH;
        }

        protected Object handleGetObject(final String key) {
            return null;
        }

        public Enumeration<String> getKeys() {
            return null;
        }
    };

    private static I18n instance;

    public static I18n getInstance() {
        return instance;
    }

    private final Locale defaultLocale = Locale.getDefault();
    private final ResourceBundle defaultBundle;
    private final JavaPlugin plugin;
    private Locale currentLocale = defaultLocale;
    private ResourceBundle localeBundle;
    private Map<String, MessageFormat> messageFormatCache = Maps.newHashMap();

    public I18n(@NotNull JavaPlugin plugin) {
        this.plugin = plugin;
        this.defaultBundle = ResourceBundle.getBundle(MESSAGES, Locale.ENGLISH, new FileResClassLoader(I18n.class.getClassLoader(), this.plugin), new UTF8PropertiesControl());
        this.localeBundle = this.defaultBundle;
    }

    public static String tl(@NotNull String string, Object... objects) {
        if (instance == null) {
            return "";
        }
        if (objects.length == 0) {
            return NODOUBLEMARK.matcher(instance.translate(string)).replaceAll("'");
        } else {
            return instance.format(string, objects);
        }
    }

    public static String capitalCase(@Nullable String input) {
        return input == null || input.length() == 0 ? input : input.toUpperCase(Locale.ENGLISH).charAt(0) + input.toLowerCase(Locale.ENGLISH).substring(1);
    }

    /**
     * Called when this plugin is enabled.
     */
    public void onEnable() {
        instance = this;
    }

    /**
     * Called when this plugin is disabled.
     */
    public void onDisable() {
        instance = null;
    }

    @NotNull
    public Locale getCurrentLocale() {
        return this.currentLocale;
    }

    private String translate(@NotNull String string) {
        try {
            try {
                return this.localeBundle.getString(string);
            } catch (MissingResourceException ex) {
                return this.defaultBundle.getString(string);
            }
        } catch (MissingResourceException ex) {
            //this.plugin.getLogger().log(Level.WARNING, String.format("Missing translation key \"%s\" in translation file %s", ex.getKey(), this.localeBundle.getLocale().toString()));
            return string;
        }
    }

    public String format(@NotNull String string, Object... objects) {
        String format = this.translate(string);
        MessageFormat messageFormat = this.messageFormatCache.get(format);
        if (messageFormat == null) {
            try {
                messageFormat = new MessageFormat(format);
            } catch (@NotNull IllegalArgumentException e) {
                this.plugin.getLogger().log(Level.SEVERE, "Invalid Translation key for ''{0}'': {1}", new Object[]{string, e.getMessage()});
                format = format.replaceAll("\\{(\\D*?)\\}", "\\[$1\\]");
                messageFormat = new MessageFormat(format);
            }
            messageFormatCache.put(format, messageFormat);
        }
        return messageFormat.format(objects).replace(' ', ' '); // replace nbsp with a space
    }

    public void updateLocale(String loc) {
        if (loc != null && !loc.isEmpty()) {
            String[] parts = loc.split("[_\\.]");
            if (parts.length == 1) {
                this.currentLocale = new Locale(parts[0]);
            }
            if (parts.length == 2) {
                this.currentLocale = new Locale(parts[0], parts[1]);
            }
            if (parts.length == 3) {
                this.currentLocale = new Locale(parts[0], parts[1], parts[2]);
            }
        }
        ResourceBundle.clearCache();
        this.messageFormatCache = Maps.newHashMap();
        this.plugin.getLogger().log(Level.INFO, String.format("Using locale %s", this.currentLocale.toString()));

        try {
            this.localeBundle = ResourceBundle.getBundle(MESSAGES, this.currentLocale, new FileResClassLoader(I18n.class.getClassLoader(), this.plugin), new UTF8PropertiesControl());
        } catch (MissingResourceException ex) {
            this.localeBundle = NULL_BUNDLE;
        }
    }

    /**
     * Attempts to load properties files from the plugin directory before
     * falling back to the jar.
     */
    private static class FileResClassLoader extends ClassLoader {

        private final transient File dataFolder;

        FileResClassLoader(@NotNull ClassLoader classLoader, @NotNull JavaPlugin plugin) {
            super(classLoader);
            this.dataFolder = new File(plugin.getDataFolder(), "localization");
        }

        @Override
        public URL getResource(@NotNull String string) {
            File file = new File(this.dataFolder, string);
            if (file.exists()) {
                try {
                    return file.toURI().toURL();
                } catch (MalformedURLException ex) {
                }
            }
            return null;
        }

        @Override
        public InputStream getResourceAsStream(@NotNull String string) {
            File file = new File(this.dataFolder, string);
            if (file.exists()) {
                try {
                    return new FileInputStream(file);
                } catch (FileNotFoundException ex) {
                }
            }
            return null;
        }
    }

    /**
     * Reads .properties files as UTF-8 instead of ISO-8859-1, which is the
     * default on Java 8/below. Java 9 fixes this by defaulting to UTF-8 for
     * .properties files.
     */
    private static class UTF8PropertiesControl extends ResourceBundle.Control {

        public ResourceBundle newBundle(@NotNull String baseName, @NotNull Locale locale, @NotNull String format, @NotNull ClassLoader loader, boolean reload) throws IOException {
            String resourceName = this.toResourceName(toBundleName(baseName, locale), "properties");
            ResourceBundle bundle = null;
            InputStream stream = null;
            if (reload) {
                URL url = loader.getResource(resourceName);
                if (url != null) {
                    URLConnection connection = url.openConnection();
                    if (connection != null) {
                        connection.setUseCaches(false);
                        stream = connection.getInputStream();
                    }
                }
            } else {
                stream = loader.getResourceAsStream(resourceName);
            }
            if (stream != null) {
                try {
                    // use UTF-8 here, this is the important bit
                    bundle = new PropertyResourceBundle(new InputStreamReader(stream, StandardCharsets.UTF_8));
                } finally {
                    stream.close();
                }
            }
            return bundle;
        }
    }

}
