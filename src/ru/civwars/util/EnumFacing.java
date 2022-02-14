package ru.civwars.util;

import com.google.common.collect.Iterators;
import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.function.Predicate;
import ru.civwars.util.math.MathHelper;
import ru.civwars.util.math.Vec3i;
import ru.lib27.annotation.NotNull;
import ru.lib27.annotation.Nullable;

public enum EnumFacing {

    DOWN(0, 1, -1, "down", AxisDirection.NEGATIVE, Axis.Y, new Vec3i(0, -1, 0)),
    UP(1, 0, -1, "up", AxisDirection.POSITIVE, Axis.Y, new Vec3i(0, 1, 0)),
    NORTH(2, 3, 2, "north", AxisDirection.NEGATIVE, Axis.Z, new Vec3i(0, 0, -1)),
    SOUTH(3, 2, 0, "south", AxisDirection.POSITIVE, Axis.Z, new Vec3i(0, 0, 1)),
    WEST(4, 5, 1, "west", AxisDirection.NEGATIVE, Axis.X, new Vec3i(-1, 0, 0)),
    EAST(5, 4, 3, "east", AxisDirection.POSITIVE, Axis.X, new Vec3i(1, 0, 0));

    public static final EnumFacing[] VALUES = new EnumFacing[6];
    public static final EnumFacing[] HORIZONTALS = new EnumFacing[4];
    private static final Map<String, EnumFacing> NAME_LOOKUP = Maps.newHashMap();

    private final int index;
    private final int opposite;
    private final int horizontalIndex;
    private final String name;
    private final Axis axis;
    private final AxisDirection axisDirection;
    private final Vec3i directionVec;

    static {
        for (EnumFacing facing : values()) {
            EnumFacing.VALUES[facing.index] = facing;
            if (facing.getAxis().isHorizontal()) {
                EnumFacing.HORIZONTALS[facing.horizontalIndex] = facing;
            }
            EnumFacing.NAME_LOOKUP.put(facing.getName2().toLowerCase(Locale.ROOT), facing);
        }
    }

    private EnumFacing(int index, int opposite, int horizontalIndex, String name, AxisDirection axisDirection, Axis axis, Vec3i directionVec) {
        this.index = index;
        this.horizontalIndex = horizontalIndex;
        this.opposite = opposite;
        this.name = name;
        this.axis = axis;
        this.axisDirection = axisDirection;
        this.directionVec = directionVec;
    }

    public int getIndex() {
        return this.index;
    }

    public int getHorizontalIndex() {
        return this.horizontalIndex;
    }

    public AxisDirection getAxisDirection() {
        return this.axisDirection;
    }

    public EnumFacing getOpposite() {
        return getFront(this.opposite);
    }

    public EnumFacing rotateAround(@NotNull Axis axis) {
        switch (axis) {
            case X: {
                if (this != EnumFacing.WEST && this != EnumFacing.EAST) {
                    return this.rotateX();
                }
                return this;
            }
            case Y: {
                if (this != EnumFacing.UP && this != EnumFacing.DOWN) {
                    return this.rotateY();
                }
                return this;
            }
            case Z: {
                if (this != EnumFacing.NORTH && this != EnumFacing.SOUTH) {
                    return this.rotateZ();
                }
                return this;
            }
            default: {
                throw new IllegalStateException("Unable to get CW facing for axis " + axis);
            }
        }
    }

    public EnumFacing rotateY() {
        switch (this) {
            case NORTH: {
                return EnumFacing.EAST;
            }
            case EAST: {
                return EnumFacing.SOUTH;
            }
            case SOUTH: {
                return EnumFacing.WEST;
            }
            case WEST: {
                return EnumFacing.NORTH;
            }
            default: {
                throw new IllegalStateException("Unable to get Y-rotated facing of " + this);
            }
        }
    }

    private EnumFacing rotateX() {
        switch (this) {
            case NORTH: {
                return EnumFacing.DOWN;
            }
            default: {
                throw new IllegalStateException("Unable to get X-rotated facing of " + this);
            }
            case SOUTH: {
                return EnumFacing.UP;
            }
            case UP: {
                return EnumFacing.NORTH;
            }
            case DOWN: {
                return EnumFacing.SOUTH;
            }
        }
    }

    private EnumFacing rotateZ() {
        switch (this) {
            case EAST: {
                return EnumFacing.DOWN;
            }
            default: {
                throw new IllegalStateException("Unable to get Z-rotated facing of " + this);
            }
            case WEST: {
                return EnumFacing.UP;
            }
            case UP: {
                return EnumFacing.EAST;
            }
            case DOWN: {
                return EnumFacing.WEST;
            }
        }
    }

    public EnumFacing rotateYCCW() {
        switch (this) {
            case NORTH: {
                return EnumFacing.WEST;
            }
            case EAST: {
                return EnumFacing.NORTH;
            }
            case SOUTH: {
                return EnumFacing.EAST;
            }
            case WEST: {
                return EnumFacing.SOUTH;
            }
            default: {
                throw new IllegalStateException("Unable to get CCW facing of " + this);
            }
        }
    }

    public int getFrontOffsetX() {
        return (this.axis == Axis.X) ? this.axisDirection.getOffset() : 0;
    }

    public int getFrontOffsetY() {
        return (this.axis == Axis.Y) ? this.axisDirection.getOffset() : 0;
    }

    public int getFrontOffsetZ() {
        return (this.axis == Axis.Z) ? this.axisDirection.getOffset() : 0;
    }

    public String getName2() {
        return this.name;
    }

    public Axis getAxis() {
        return this.axis;
    }

    @Nullable
    public static EnumFacing byName(String name) {
        return (name == null) ? null : EnumFacing.NAME_LOOKUP.get(name.toLowerCase(Locale.ROOT));
    }

    public static EnumFacing getFront(int index) {
        return EnumFacing.VALUES[MathHelper.abs(index % EnumFacing.VALUES.length)];
    }

    public static EnumFacing getHorizontal(int index) {
        return EnumFacing.HORIZONTALS[MathHelper.abs(index % EnumFacing.HORIZONTALS.length)];
    }

    public static EnumFacing fromAngle(double angle) {
        return getHorizontal(MathHelper.floor(angle / 90.0 + 0.5) & 0x3);
    }

    public float getHorizontalAngle() {
        return (float) ((this.horizontalIndex & 0x3) * 90);
    }

    public static EnumFacing random(@NotNull Random radnom) {
        return values()[radnom.nextInt(values().length)];
    }

    public static EnumFacing getFacingFromVector(float i1, float i2, float i3) {
        EnumFacing enumfacing = EnumFacing.NORTH;
        float f = Float.MIN_VALUE;
        for (final EnumFacing enumfacing2 : values()) {
            final float f2 = i1 * enumfacing2.directionVec.getX() + i2 * enumfacing2.directionVec.getY() + i3 * enumfacing2.directionVec.getZ();
            if (f2 > f) {
                f = f2;
                enumfacing = enumfacing2;
            }
        }
        return enumfacing;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public String getName() {
        return this.name;
    }

    public static EnumFacing getFacingFromAxis(@NotNull AxisDirection direction, @NotNull Axis axis) {
        for (EnumFacing facing : values()) {
            if (facing.getAxisDirection() == direction && facing.getAxis() == axis) {
                return facing;
            }
        }
        throw new IllegalArgumentException("No such direction: " + direction + " " + axis);
    }

    public Vec3i getDirectionVec() {
        return this.directionVec;
    }

    public enum Axis implements Predicate<EnumFacing> {
        X("x", Plane.HORIZONTAL),
        Y("y", Plane.VERTICAL),
        Z("z", Plane.HORIZONTAL);

        private static final Map<String, Axis> NAME_LOOKUP = Maps.newHashMap();

        static {
            for (Axis axis : values()) {
                Axis.NAME_LOOKUP.put(axis.getName2().toLowerCase(Locale.ROOT), axis);
            }
        }

        private final String name;
        private final Plane plane;

        private Axis(@NotNull String name, @NotNull Plane plane) {
            this.name = name;
            this.plane = plane;
        }

        @Nullable
        public static Axis byName(String name) {
            return (name == null) ? null : Axis.NAME_LOOKUP.get(name.toLowerCase(Locale.ROOT));
        }

        public String getName2() {
            return this.name;
        }

        public boolean isVertical() {
            return this.plane == Plane.VERTICAL;
        }

        public boolean isHorizontal() {
            return this.plane == Plane.HORIZONTAL;
        }

        @Override
        public String toString() {
            return this.name;
        }

        @Override
        public boolean test(@Nullable EnumFacing facing) {
            return facing != null && facing.getAxis() == this;
        }

        public Plane getPlane() {
            return this.plane;
        }

        public String getName() {
            return this.name;
        }
    }

    public enum AxisDirection {
        POSITIVE(1, "Towards positive"),
        NEGATIVE(-1, "Towards negative");

        private final int offset;
        private final String description;

        private AxisDirection(int offset, @NotNull String description) {
            this.offset = offset;
            this.description = description;
        }

        public int getOffset() {
            return this.offset;
        }

        @Override
        public String toString() {
            return this.description;
        }
    }

    public enum Plane implements Predicate<EnumFacing>, Iterable<EnumFacing> {
        HORIZONTAL,
        VERTICAL;

        public EnumFacing[] facings() {
            switch (this) {
                case HORIZONTAL: {
                    return new EnumFacing[]{EnumFacing.NORTH, EnumFacing.EAST, EnumFacing.SOUTH, EnumFacing.WEST};
                }
                case VERTICAL: {
                    return new EnumFacing[]{EnumFacing.UP, EnumFacing.DOWN};
                }
                default: {
                    throw new Error("Someone's been tampering with the universe!");
                }
            }
        }

        @NotNull
        public EnumFacing random(@NotNull Random random) {
            EnumFacing[] facings = this.facings();
            return facings[random.nextInt(facings.length)];
        }

        @Override
        public boolean test(@Nullable EnumFacing facing) {
            return facing != null && facing.getAxis().getPlane() == this;
        }

        @Override
        public Iterator<EnumFacing> iterator() {
            return (Iterator<EnumFacing>) Iterators.forArray(this.facings());
        }
    }

}
