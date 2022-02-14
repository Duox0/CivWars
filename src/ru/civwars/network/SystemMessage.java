package ru.civwars.network;

import ru.lib27.annotation.NotNull;

public enum SystemMessage {
    
    FACTION_NAME_INVALID(100),
    FACTION_NAME_TOO_SHORT(101),
    FACTION_NAME_TOO_LONG(102),
    FACTION_NAME_ALREADY_EXIST(103),
    FACTION_NOT_CREATED(104),
    FACTION_CREATE(105),
    ALREADY_IN_FACTION(107),
    ALREADY_IN_FACTION_S(108),
    FACTION_NOT_ENOUGH_ITEM(109),
    
    TOWN_NAME_INVALID(200),
    TOWN_NAME_TOO_SHORT(201),
    TOWN_NAME_TOO_LONG(202),
    TOWN_NAME_ALREADY_EXIST(203),
    TOWN_NOT_ENOUGH_ITEM(204),
    TOWN_NOT_CREATED(205),
    TOWN_CREATE(206),
    ALREADY_IN_TOWN(207),
    ALREADY_IN_TOWN_S(208),
    
    STRUCTURE_VALIDATOR_CHECKING_POSITION(220),
    STRUCTURE_VALIDATOR_SUCCESS(221),
    STRUCTURE_VALIDATOR_FAILED(222),
    STRUCTURE_VALIDATOR_INVALID_LAYER(223)
    
    ;
    
    private final String key;
    private SystemMessage(int key) {
        this.key = "" + key;
    }
    
    @NotNull
    public String getKey() {
        return this.key;
    }
    
}
