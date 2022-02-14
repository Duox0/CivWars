package ru.civwars.util.registry;

import com.google.common.collect.Maps;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.Validate;
import ru.lib27.annotation.NotNull;
import ru.lib27.annotation.Nullable;

public class RegistrySimple<K, V> {

    protected final Map<K, V> objects;

    public RegistrySimple() {
        this.objects = this.createUnderlyingMap();
    }

    public Map<K, V> createUnderlyingMap() {
        return Maps.newHashMap();
    }

    public void putObject(@NotNull K key, @NotNull V value) {
        Validate.notNull(key);
        Validate.notNull(value);
        if (this.objects.containsKey(key)) {
            Logger.getLogger(RegistrySimple.class.getSimpleName()).log(Level.WARNING, "Adding duplicate key '" + key + "' to registry");
        }
        this.objects.put(key, value);
    }

    @Nullable
    public V getObject(@Nullable K key) {
        return this.objects.get(key);
    }

    public boolean containsKey(K key) {
        return this.objects.containsKey(key);
    }

    @NotNull
    public Set<K> getKeys() {
        return Collections.unmodifiableSet(this.objects.keySet());
    }

    @NotNull
    public Iterator<V> iterator() {
        return this.objects.values().iterator();
    }
}
