package cambeeler;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public
class IndexFile
{
    private static IndexFile instance = new IndexFile();  //Singleton Object
    private String dataFileName;
    private int nodeCount;
    private long beginidx;
    private List<idxData> nodeidx = new ArrayList<idxData>();

//    THERE CAN BE ONLY ONE IDX FILE in this scenario

    private IndexFile()
    { }

    public static
    IndexFile getInstance()
    {return instance;}

    public
    List<idxData> getNodeidx()
    {
        return nodeidx;
    }

    public
    String getDataFileName()
    {
        return dataFileName;
    }

    public
    void setDataFileName(String dataFileName)
    {
        this.dataFileName = dataFileName;
    }

    public
    int getNodeCount()
    {
        return nodeCount;
    }

    public
    void setNodeCount(int nodeCount)
    {
        this.nodeCount = nodeCount;
    }

    public
    long getBeginidx()
    {
        return beginidx;
    }

    public
    void setBeginidx(long beginidx)
    {
        this.beginidx = beginidx;
    }
}
