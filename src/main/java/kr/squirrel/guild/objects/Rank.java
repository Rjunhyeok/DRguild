package kr.squirrel.guild.objects;

public enum Rank {


    MASTER("§a길드 마스터"),
    SUB_MASTER("§a길드 부마스터"),
    MEMBER("§a길드원");

    private String name;

    Rank(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
