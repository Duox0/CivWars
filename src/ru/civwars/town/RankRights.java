package ru.civwars.town;

public class RankRights {

    public static final long RIGHT_EMPTY = 0;
    public static final long TR_RIGHT_CHAT_LISTEN = RIGHT_EMPTY | 1;
    public static final long TR_RIGHT_CHAT_SPEAK = RIGHT_EMPTY | 2;
    
    public static final long TR_RIGHT_SETNAME =  RIGHT_EMPTY | 4; 
    public static final long TR_RIGHT_SETLEADER =  RIGHT_EMPTY | 8; 
    
    public static final long TR_RIGHT_INVITE = RIGHT_EMPTY | 16;
    public static final long TR_RIGHT_KICK = RIGHT_EMPTY | 32;
    
    public static final long TR_RIGHT_PROMOTE = RIGHT_EMPTY | 64;
    public static final long TR_RIGHT_DEMOTE = RIGHT_EMPTY | 128;
    
    public static final long TR_RIGHT_WITHDRAW_GOLD = RIGHT_EMPTY | 256;

    public static final long RIGHTS_TOWN_MEMBER = TR_RIGHT_CHAT_LISTEN | TR_RIGHT_CHAT_SPEAK;
    public static final long RIGHTS_TOWN_OFFICER = RIGHTS_TOWN_MEMBER;
    public static final long RIGHTS_TOWN_MASTER = RIGHTS_TOWN_OFFICER;

    public static final long RIGHTS_CIV_OFFICER = RIGHT_EMPTY;
    public static final long RIGHTS_CIV_MASTER = RIGHTS_CIV_OFFICER;
}
