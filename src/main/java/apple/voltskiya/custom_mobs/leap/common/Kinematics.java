package apple.voltskiya.custom_mobs.leap.common;

public interface Kinematics {

    static double velocityInitial(double velocityFinal, double acceleration, double dx) {
        return Math.sqrt(velocityFinal * velocityFinal - 2 * acceleration * dx);
    }

    static double velocityInitialWithTime(double distance, double time, double acceleration) {
        return distance / time - acceleration * time / 2;
    }

    static double magnitude(double a, double b) {
        return Math.sqrt(a * a + b * b);
    }

    static double timeVerifyVelocity(double distance, double velocityInitial, double acceleration) {
        double velocityInitialSquared = velocityInitial * velocityInitial;
        double b = 2 * acceleration * distance;
        if (velocityInitialSquared + b < 0) {
            // b is never positive (unless someone flipped gravity)
            return Math.nextUp(Math.sqrt(Math.abs(b)));
        }
        return velocityInitial;
    }

    static PlusOrMinus time(double distance, double velocityInitialY, double acceleration) {
        double c = velocityInitialY * velocityInitialY + 2 * acceleration * distance;

        if (c < 0)
            return PlusOrMinus.FAIL;
        double computed = Math.sqrt(c);
        double plus = (-velocityInitialY + computed) / acceleration;
        double minus = (-velocityInitialY - computed) / acceleration;
        return new PlusOrMinus(plus, minus);
    }
}
