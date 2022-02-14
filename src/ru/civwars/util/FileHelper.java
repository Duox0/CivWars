package ru.civwars.util;

import com.google.common.collect.Lists;
import java.io.File;
import java.util.List;
import java.util.function.Predicate;
import java.util.logging.Logger;
import ru.civwars.CivWars;
import ru.lib27.annotation.NotNull;
import ru.lib27.annotation.Nullable;

public class FileHelper {

    private static final Logger LOGGER = Logger.getLogger(CivWars.ID);

    private FileHelper() {
    }

    @NotNull
    public static List<File> files(@NotNull File folder, boolean flag, @Nullable Predicate<File> filter) {
        if (!(folder.exists() && folder.isDirectory())) {
            return Lists.newArrayList();
        }

        List<File> files = Lists.newArrayList();
        for (File file : folder.listFiles()) {
            if (file.isFile()) {
                if (filter == null || filter.test(file)) {
                    files.add(file);
                }
            } else if (flag && file.isDirectory()) {
                files.addAll(files(file, flag, filter));
            }
        }
        return files;
    }

    @NotNull
    public static List<File> files(@NotNull File folder, boolean flag) {
        return files(folder, flag, null);
    }

}
