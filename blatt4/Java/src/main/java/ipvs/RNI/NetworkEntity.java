package ipvs.RNI;

public abstract class NetworkEntity {
    private static int globalID = 0;

    private int id;

    public NetworkEntity()
    {
        id = globalID++;
    }

    public int getAddress() {
        return id;
    }

    public abstract void registerInput(DelayDataRateChannel input);
    public abstract void registerOutput(DelayDataRateChannel output);

    public abstract void processEvents();
}
