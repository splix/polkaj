package io.emeraldpay.polkaj.tx;

public interface Era {

    public static final Era IMMORTAL = new Era.Immortal();

    public byte[] encode();

    public Integer toInteger();

    /**
     * Get the block number of the start of the era whose properties this object
     * describes that `current` belongs to.
     *
     * @param current current block
     * @return target block number
     */
    public long birth(long current);

    /**
     * Get the block number of the first block at which the era has ended.
     *
     * @param current current block
     * @return target block number
     */
    public long death(long current);

    public boolean isImmortal();

    public static Era decode(int value) {
        if (value < 0) {
            throw new IllegalArgumentException("Era value cannot be negative: " + value);
        }
        if (value > 0xffff) {
            throw new IllegalArgumentException("Era value is too large: " + value);
        }
        if (value == 0) {
            return new Immortal();
        }
        int period = 2 << (value % (1 << 4));
        int quantizeFactor = Math.max(1, period >> 12);
        int phase = (value >> 4) * quantizeFactor;
        if (period >= 4 && phase < period) {
            return new Mortal(period, phase);
        } else {
            throw new IllegalArgumentException("Invalid period or phase");
        }
    }

    public static class Immortal implements Era {

        @Override
        public byte[] encode() {
            return new byte[] {0};
        }

        @Override
        public Integer toInteger() {
            return 0;
        }

        @Override
        public long birth(long current) {
            return 0;
        }

        @Override
        public long death(long current) {
            return Long.MAX_VALUE;
        }

        @Override
        public boolean isImmortal() {
            return true;
        }
    }

    public static class Mortal implements Era {
        private final long period;
        private final long phase;

        public Mortal(long period, long phase) {
            if (period < 0 || phase < 0) {
                throw new IllegalArgumentException("Invalid era: " + period + ", " + phase);
            }
            this.period = period;
            this.phase = phase;
        }

        public static Mortal forCurrent(long period, long current) {
            int power = period == 0 ? 0 : 64 - Long.numberOfLeadingZeros(period - 1);

            period = Math.min(1L << 16, Math.max(4, Math.round(Math.pow(2, power))));
            long phase = current % period;
            long quantizeFactor = Math.max(1, period >> 12);
            long quantizePhase = Math.floorDiv(phase, quantizeFactor) * quantizeFactor;
            return new Mortal(period, quantizePhase);
        }

        public long getPeriod() {
            return period;
        }

        public long getPhase() {
            return phase;
        }

        @Override
        public byte[] encode() {
            long quantizeFactor = Math.max(1L, period >> 12);
            long result = Math.min(15, Math.max(1, Long.numberOfTrailingZeros(period) - 1));
            result += (phase / quantizeFactor) << 4;
            return new byte[] {
                    (byte)((result >> 8) & 0xff),
                    (byte)(result & 0xff),
            };
        }

        @Override
        public Integer toInteger() {
            byte[] encoded = this.encode();
            return Byte.toUnsignedInt(encoded[0]) << 8 | Byte.toUnsignedInt(encoded[1]);
        }

        @Override
        public long birth(long current) {
            current = Math.max(current, this.phase);
            return Math.floorDiv(current - phase, this.period) * this.period + phase;
        }

        @Override
        public long death(long current) {
            return this.birth(current) + period;
        }

        @Override
        public boolean isImmortal() {
            return false;
        }
    }

}
