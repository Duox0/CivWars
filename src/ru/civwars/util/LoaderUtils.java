package ru.civwars.util;

import com.google.common.collect.Lists;
import java.io.File;
import java.util.List;
import ru.lib27.annotation.NotNull;

public class LoaderUtils {
    
    @NotNull
    public static List<File> getFiles(@NotNull File folder, boolean flag) {
        if(!(folder.exists() && folder.isDirectory())) {
            return Lists.newArrayList();
        }
        
        List<File> files = Lists.newArrayList();
        for(File file : folder.listFiles()) {
            if(file.isFile()) {
                files.add(file);
            } else if(flag && file.isDirectory()) {
                files.addAll(getFiles(file, true));
            }
        }
        return files;
    }
    
}
