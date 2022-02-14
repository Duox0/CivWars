package ru.civwars.util;

public class Utils {
    
    public static boolean isBoolean(String value) {
        if(value == null) {
            return false;
        }
        
        value = value.toLowerCase();
        
        switch(value) {
            case "true":
            case "1":
            case "false":
            case "0":
                return true;
        }
        return false;
    }
    
    public static boolean getAsBoolean(String value) {
        if(!Utils.isBoolean(value)) {
            return false;
        }
        
        value = value.toLowerCase();
        switch(value) {
            case "true":
            case "1":
                return true;
        }
        return false;
    }
    
    public static boolean isByte(String value) {
        if(value == null) {
            return false;
        }
        try {
            Byte.parseByte(value);
            return true;
        } catch(NumberFormatException ex) {
            return false;
        }
    }
    
    public static byte getAsByte(String value) {
        if(!Utils.isByte(value)) {
            return 0;
        }
        try {
            byte i = Byte.parseByte(value);
            return i;
        } catch(NumberFormatException ex) {
            return 0;
        }
    }

    public static boolean isShort(String value) {
        if(value == null) {
            return false;
        }
        try {
            Short.parseShort(value);
            return true;
        } catch(NumberFormatException ex) {
            return false;
        }
    }
    
    public static short getAsShort(String value) {
        if(!Utils.isShort(value)) {
            return 0;
        }
        try {
            short i = Short.parseShort(value);
            return i;
        } catch(NumberFormatException ex) {
            return 0;
        }
    }

    public static boolean isInt(String value) {
        if(value == null) {
            return false;
        }
        try {
            Integer.parseInt(value);
            return true;
        } catch(NumberFormatException ex) {
            return false;
        }
    }
    
    public static int getAsInt(String value) {
        if(!Utils.isInt(value)) {
            return 0;
        }
        try {
            int i = Integer.parseInt(value);
            return i;
        } catch(NumberFormatException ex) {
            return 0;
        }
    }

    public static boolean isLong(String value) {
        if(value == null) {
            return false;
        }
        try {
            Long.parseLong(value);
            return true;
        } catch(NumberFormatException ex) {
            return false;
        }
    }
    
    public static long getAsLong(String value) {
        if(!Utils.isLong(value)) {
            return 0;
        }
        try {
            long i = Long.parseLong(value);
            return i;
        } catch(NumberFormatException ex) {
            return 0;
        }
    }

    public static boolean isFloat(String value) {
        if(value == null) {
            return false;
        }
        try {
            Float.parseFloat(value);
            return true;
        } catch(NumberFormatException ex) {
            return false;
        }
    }
    
    public static float getAsFloat(String value) {
        if(!Utils.isFloat(value)) {
            return 0;
        }
        try {
            float i = Float.parseFloat(value);
            return i;
        } catch(NumberFormatException ex) {
            return 0;
        }
    }

    public static boolean isDouble(String value) {
        if(value == null) {
            return false;
        }
        try {
            Double.parseDouble(value);
            return true;
        } catch(NumberFormatException ex) {
            return false;
        }
    }
    
    public static double getAsDouble(String value) {
        if(!Utils.isDouble(value)) {
            return 0;
        }
        try {
            double i = Double.parseDouble(value);
            return i;
        } catch(NumberFormatException ex) {
            return 0;
        }
    }

    
}
