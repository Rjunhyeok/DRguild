package kr.squirrel.guild.objects.run;

import kr.squirrel.guild.objects.LocationDTO;

import java.util.UUID;

public class GuildRunUser {

    private UUID uuid;
    private LocationDTO previousLocation;

    public GuildRunUser(UUID uuid, LocationDTO previousLocation) {
        this.uuid = uuid;
        this.previousLocation = previousLocation;
    }

    public LocationDTO getPreviousLocation() {
        return previousLocation;
    }

    public UUID getUUID() {
        return uuid;
    }
}
