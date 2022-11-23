package apple.voltskiya.custom_mobs.leap.common;

public final class PlusOrMinus {

    public static final PlusOrMinus FAIL = new PlusOrMinus(Double.NaN, Double.NaN);
    private final double plus;
    private final double minus;
    private Double min;
    private Double max;

    public PlusOrMinus(double plus, double minus) {
        this.plus = plus;
        this.minus = minus;
    }

    public PlusOrMinus(PlusOrMinus a, PlusOrMinus b) {
        this(max(a.max(), b.max()), min(a.min(), b.min()));
    }

    public double min() {
        if (this.min != null)
            return this.min;
        return this.min = min(plus, minus);
    }


    public double max() {
        if (this.max != null)
            return this.max;
        return this.max = max(plus, minus);
    }

    private static double min(double a, double b) {
        if (Double.isNaN(a))
            return b;
        if (Double.isNaN(b))
            return a;
        return Math.min(a, b);

    }

    private static double max(double a, double b) {
        if (Double.isNaN(a))
            return b;
        if (Double.isNaN(b))
            return a;
        return Math.max(a, b);
    }

    public boolean isFail() {
        return this == FAIL || (Double.isNaN(this.plus) && Double.isNaN(this.minus));
    }

    @Override
    public String toString() {
        return "PlusOrMinus[" +
            "plus=" + plus + ", " +
            "minus=" + minus + ']';
    }

    public boolean isEitherFail() {
        return (Double.isNaN(this.plus) || Double.isNaN(this.minus));
    }
}
