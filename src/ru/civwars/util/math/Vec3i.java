package ru.civwars.util.math;

import com.google.common.base.MoreObjects;
import ru.lib27.annotation.NotNull;

public class Vec3i implements Comparable<Vec3i> {

    public static final Vec3i NULL_VECTOR = new Vec3i(0, 0, 0);

    private final int x;
    private final int y;
    private final int z;

    public Vec3i(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vec3i(double x, double y, double z) {
        this(MathHelper.floor(x), MathHelper.floor(y), MathHelper.floor(z));
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getZ() {
        return this.z;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof Vec3i)) {
            return false;
        }
        Vec3i other = (Vec3i) object;
        return this.getX() == other.getX() && this.getY() == other.getY() && this.getZ() == other.getZ();
    }

    @Override
    public int hashCode() {
        return (this.getY() + this.getZ() * 31) * 31 + this.getX();
    }

    @Override
    public int compareTo(Vec3i object) {
        if (this.getY() != object.getY()) {
            return this.getY() - object.getY();
        }
        if (this.getZ() == object.getZ()) {
            return this.getX() - object.getX();
        }
        return this.getZ() - object.getZ();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper((Object) this).add("x", this.getX()).add("y", this.getY()).add("z", this.getZ()).toString();
    }

    @NotNull
    public Vec3i crossProduct(@NotNull Vec3i vec) {
        return new Vec3i(this.getY() * vec.getZ() - this.getZ() * vec.getY(), this.getZ() * vec.getX() - this.getX() * vec.getZ(), this.getX() * vec.getY() - this.getY() * vec.getX());
    }

    public double getDistance(int x, int y, int z) {
        double xx = this.getX() - x;
        double yy = this.getY() - y;
        double zz = this.getZ() - z;
        return Math.sqrt(xx * xx + yy * yy + zz * zz);
    }

    public double distanceSq(double x, double y, double z) {
        double xx = this.getX() - x;
        double yy = this.getY() - y;
        double zz = this.getZ() - z;
        return xx * xx + yy * yy + zz * zz;
    }

    public double distanceSqToCenter(double x, double y, double z) {
        double xx = this.getX() + 0.5 - x;
        double yy = this.getY() + 0.5 - y;
        double zz = this.getZ() + 0.5 - z;
        return xx * xx + yy * yy + zz * zz;
    }

    public double distanceSq(@NotNull Vec3i vec) {
        return this.distanceSq(vec.getX(), vec.getY(), vec.getZ());
    }

}
