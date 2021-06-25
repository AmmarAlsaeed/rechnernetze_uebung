package ipvs.RNI;

public class Clock {
    private static int time = 0;
    public static final int discretizationStep = 125; //[ms]

    private static void resetClock()
    {
        time = 0;
    }

    public static void elapseTime()
    {
        time++;
        try {
            Thread.sleep(discretizationStep);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("==== " + time + " ====");
    }

    public static int getTime() {
        return time;
    }
}
