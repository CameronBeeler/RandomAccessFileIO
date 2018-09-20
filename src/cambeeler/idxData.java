package cambeeler;

public
class idxData
{
    private int node;
    private long offset;

    public
    idxData(int node, long offset)
    {
        this.node = node;
        this.offset = offset;
    }

    public
    idxData(int node)
    {
        this.node = node;
        this.offset = 0;
    }

    public
    int getNode()
    {
        return node;
    }

    public
    long getOffset()
    {
        return offset;
    }

    public
    void setOffset(long offset)
    {
        this.offset = offset;
    }

    @Override
    public
    String toString()
    {
        return "idxData{" +
               "node=" + node +
               ", offset=" + offset +
               '}';
    }
}
