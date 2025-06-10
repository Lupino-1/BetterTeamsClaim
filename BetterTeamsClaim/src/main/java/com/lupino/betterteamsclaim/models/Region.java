package com.lupino.betterteamsclaim.models;


import com.booksaw.betterTeams.Team;
import org.bukkit.Location;


public class Region {


    private final Team team;
    private final Location corner1;
    private final Location corner2;



    public Region(Team team, Location corner1, Location corner2) {
        this.team = team;
        this.corner1 = corner1;
        this.corner2 = corner2;


    }

    public Team getTeam() {
        return team;
    }
    public boolean isInside(Location loc) {

        Location loc1 = getCorner1().getBlock().getLocation();
        Location loc2 = getCorner2().getBlock().getLocation();
        // Zjištění minimálních a maximálních souřadnic pro každou osu (X, Y, Z)
        double minX = Math.min(loc1.getX(), loc2.getX());
        double maxX = Math.max(loc1.getX(), loc2.getX());

        double minY = Math.min(loc1.getY(), loc2.getY());
        double maxY = Math.max(loc1.getY(), loc2.getY());

        double minZ = Math.min(loc1.getZ(), loc2.getZ());
        double maxZ = Math.max(loc1.getZ(), loc2.getZ());

        // Kontrola, jestli je hráčova pozice mezi těmito hodnotami
        return loc.getX() >= minX && loc.getX() <= maxX &&
                loc.getY() >= minY && loc.getY() <= maxY &&
                loc.getZ() >= minZ && loc.getZ() <= maxZ;
    }
    public boolean intersects(Region other) {
        if (other == null || corner1.getWorld() == null || !corner1.getWorld().equals(other.getCorner1().getWorld())) {
            return false;
        }

        double x1Min = Math.min(corner1.getX(), corner2.getX());
        double x1Max = Math.max(corner1.getX(), corner2.getX());
        double y1Min = Math.min(corner1.getY(), corner2.getY());
        double y1Max = Math.max(corner1.getY(), corner2.getY());
        double z1Min = Math.min(corner1.getZ(), corner2.getZ());
        double z1Max = Math.max(corner1.getZ(), corner2.getZ());

        double x2Min = Math.min(other.getCorner1().getX(), other.getCorner2().getX());
        double x2Max = Math.max(other.getCorner1().getX(), other.getCorner2().getX());
        double y2Min = Math.min(other.getCorner1().getY(), other.getCorner2().getY());
        double y2Max = Math.max(other.getCorner1().getY(), other.getCorner2().getY());
        double z2Min = Math.min(other.getCorner1().getZ(), other.getCorner2().getZ());
        double z2Max = Math.max(other.getCorner1().getZ(), other.getCorner2().getZ());

        return (x1Min <= x2Max && x1Max >= x2Min) &&
                (y1Min <= y2Max && y1Max >= y2Min) &&
                (z1Min <= z2Max && z1Max >= z2Min);
    }
    public boolean intersects(Location loc1,Location loc2) {
        if (corner1.getWorld() == null || !corner1.getWorld().equals(loc1.getWorld())) {
            return false;
        }

        double x1Min = Math.min(corner1.getX(), corner2.getX());
        double x1Max = Math.max(corner1.getX(), corner2.getX());
        double y1Min = Math.min(corner1.getY(), corner2.getY());
        double y1Max = Math.max(corner1.getY(), corner2.getY());
        double z1Min = Math.min(corner1.getZ(), corner2.getZ());
        double z1Max = Math.max(corner1.getZ(), corner2.getZ());

        double x2Min = Math.min(loc1.getX(), loc2.getX());
        double x2Max = Math.max(loc1.getX(), loc2.getX());
        double y2Min = Math.min(loc1.getY(), loc2.getY());
        double y2Max = Math.max(loc1.getY(), loc2.getY());
        double z2Min = Math.min(loc1.getZ(), loc2.getZ());
        double z2Max = Math.max(loc1.getZ(), loc2.getZ());

        return (x1Min <= x2Max && x1Max >= x2Min) &&
                (y1Min <= y2Max && y1Max >= y2Min) &&
                (z1Min <= z2Max && z1Max >= z2Min);
    }

    public Location getCorner1() {
        return corner1;
    }

    public Location getCorner2() {
        return corner2;
    }
}
