package kr.squirrel.guild.objects.run;

import java.util.List;
import java.util.UUID;

public class MatchingGuild {

    private String guild;
    private List<GuildRunUser> players;


    public MatchingGuild(String guild, List<GuildRunUser> players) {
        this.guild = guild;
        this.players = players;
    }

    public String getGuild() {
        return guild;
    }

    public void setGuild(String guild) {
        this.guild = guild;
    }

    public List<GuildRunUser> getPlayers() {
        return players;
    }

    public void setPlayers(List<GuildRunUser> players) {
        this.players = players;
    }
}
