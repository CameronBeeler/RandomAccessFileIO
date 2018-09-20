package cambeeler;

import java.io.*;
import java.util.*;

public
class Locations
implements Map<Integer, Location>
{
    private static Map<Integer, Location> locations = new LinkedHashMap<Integer, Location>();
    private static IndexFile ref = IndexFile.getInstance();
    private static String FILENAME = "Locations.txt", DIRECTIONMAP = "Directions.txt";
    private static String BINFILENAME = "Locations.dat";
    private static String OBJFILENAME = "Locations.obj";
    private static String RAFINDEX = "Locations.idx";
    private static String RAFDATA = "Locations.raf";

    public static
    void main(String[] args)
    throws IOException
    {
        Location tmp = getRAFLocationData(64);
        System.out.println(tmp);

    }


//    SIMPLE BUFFERED READER CODE REVIEW
    static

    {
//    first I must add the data file to read into the game
        try
        {
//            loadFromTextFile(); //- Load up the locations array to build other data files with it...
//            writeSerialObject();  // - Create the Object file version to build...it is easier to manipulate ...
//            readSerialObject();  //  load up locations from this file to create the idx & data files...

            try (RandomAccessFile idx = new RandomAccessFile(RAFINDEX, "rwd");
                 RandomAccessFile data = new RandomAccessFile(RAFDATA, "rwd")
            )
            {
//                createIndexedFiles();
//                indexFilePrint(idx);
//                dataFilePrint(data);
//                printIndexArrayList();

                indexFileLoad(idx);
            }
            catch(IOException i)
            {
                i.printStackTrace();
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }


    }

    public static
    void createIndexedFiles()
    {
        int       nodeCount         = locations.size();
        String    indexFileLocation = RAFINDEX;
        String    dataFileLocation  = RAFDATA;
        long      idxFileOffset, dataFileOffset;
        long      idxFileEnd, dataFileEnd;

        try (RandomAccessFile idx = new RandomAccessFile(RAFINDEX, "rwd");
             RandomAccessFile data = new RandomAccessFile(RAFDATA, "rwd")
        )
        {
//          INDEX FILE HEADER
            idx.writeUTF(RAFDATA); // name of the data file in the idx file (first 2 bytes write string size)
            idx.writeInt(nodeCount); // the number of bytes = 4
            idxFileOffset = idx.getFilePointer();

//          IN MEMORY INDEX FILE
            ref.setDataFileName(RAFDATA);  // in memory
            ref.setNodeCount(locations.size()); // in memory

//          CONSTRUCTED THE DATAFILE AND THE IN-MEMORY INDEX
            List<idxData> indexDataArray = ref.getNodeidx();
            data.writeUTF(RAFINDEX);
            data.writeInt(nodeCount);
            dataFileOffset = data.getFilePointer();
            for(Location L:locations.values())
            {
                indexDataArray.add(new idxData(L.getLocationID(), (long) data.getFilePointer()));
                data.writeInt(L.getLocationID());
                data.writeUTF(L.getDescription());
               for(String dir:L.getExits().keySet())
               {
                   //DIR
                   //dest-NODE
                       data.writeUTF(dir);
                       data.writeInt(L.getExits().get(dir));
                   if(dir.equalsIgnoreCase("Q"))
                   { continue; }
               }

            }
//                data.writeUTF(L.getDatafile());
            dataFileEnd = data.getFilePointer();

            idxFileOffset = idx.getFilePointer();
            ref.setBeginidx(idx.getFilePointer());

//        ONE TIME :: CREATE THE INDEX FILE FROM THE IN-MEMORY INDEX ARRAY
            for (int i:locations.keySet())
            {
                idx.writeInt(indexDataArray.get(i).getNode());
                idx.writeLong(indexDataArray.get(i).getOffset());
            }

        }
        catch (FileNotFoundException e)
        {
            System.out.println("FileNotFoundException caught here");
            e.printStackTrace();
        }
        catch (IOException i)
        {
            System.out.println("IOException caught here");
            i.printStackTrace();
        }
    }

//    todo - not finished here...IndexFileLoad
    public static
    void indexFileLoad(RandomAccessFile idx)
    {
        try
        {
            ref.setDataFileName(idx.readUTF());
            ref.setNodeCount(idx.readInt());
            ref.setBeginidx(idx.getFilePointer());
            int incr = 0, node;
            long offset = 0;
            while(true)
            {
                node=idx.readInt();
                offset = idx.readLong();
                ref.getNodeidx().add(new idxData(node, offset));
            }

        }
        catch(EOFException eof)
        {
            System.out.println("Caught the EOF, let it go.....");
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

    }

    public static
    void printIndexArrayList()
    {
        for(idxData node: ref.getNodeidx())
        {
            System.out.println(node);
        }
    }

    public static
    Location getRAFLocationData(int node)
    {
        Location loc = null;
//        use the node to identify the offset
        long RAFDataOffset = IndexFile
                .getInstance()
                .getNodeidx()
                .get(node)
                .getOffset();
//        todo
//        access the data file at the offset
        try(RandomAccessFile raf = new RandomAccessFile(RAFDATA, "rwd"))
        {
            raf.seek(RAFDataOffset);
            loc = new Location(raf.readInt(), raf.readUTF());
            String dir;
            int destNode;
            while(true)
            {
                dir = raf.readUTF();
                destNode = raf.readInt();
                loc.addExit(dir, destNode);
                if(dir.equalsIgnoreCase("Q"))
                {
                    return loc;
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return loc;
    }

    public static
    void indexFilePrint(RandomAccessFile idx)
    {
       try
       {
           System.out.println("Data File Name: " + idx.readUTF());
           int nodes = idx.readInt();
           System.out.println("Total number of nodes: " + nodes);
           System.out.println("Nodes :: Offset");

           for (int i=0; i<nodes;i++)
           {
               System.out.println(idx.readInt() + " :: " + idx.readLong());
           }

       }
       catch(IOException e)
       {
           e.printStackTrace();
       }

    }
    public static
    void dataFilePrint(RandomAccessFile data)
    {
        try
        {
            System.out.println("Index File Name: " + data.readUTF());
            int nodes = data.readInt();
            System.out.println("Total number of nodes: " + nodes);
            System.out.println("Node :: DATA");

            String dir = null;
            int destNode;

            for(int i=0;i<nodes;i++)
            {
                System.out.println(data.readInt() + " :: " + data.readUTF());
                while(true)
                {
                    dir = data.readUTF();
                    destNode = data.readInt();
                    System.out.println(dir + " :: " + destNode);

                    if(dir.equalsIgnoreCase("Q"))
                    {
                        dir=null;
                        destNode= 0;
                        break;
                    }
                    dir=null;
                    destNode= 0;
                }
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

    }

    public static
    void printLocationsArray()
    {
//        PRINT OUT ALL LOCATIONS & EXITS
        for(Location l:locations.values())
        {
            System.out.println(l);
        }
    }

    public static
    void readSerialObject()
    throws Exception
    {
        locations.clear();
        boolean eof = false;
        try(ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(OBJFILENAME))))
        {
            while (!eof)
            {
                try
                {
                    Location location = (Location) ois.readObject();
//                    System.out.println(location);
                    locations.put(location.getLocationID(), location);
                }
                catch (EOFException e)
                {
                    eof = true;
                }
            }
        }


    }

    public static
    void writeSerialObject()
    throws Exception
    {
        try(ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(OBJFILENAME))))
        {
            for(Location l: locations.values())
            {
                oos.writeObject(l);
                System.out.println(l);
            }
        }
    }

    public static
    void loadFromTextFile()
    {
        try(BufferedReader br = new BufferedReader(new FileReader(FILENAME));)
        {
            String sTemp, input;
            Integer iTemp;
            while((input = br.readLine()) != null)
            {
                String inputData[] = input.split(",");
                iTemp = Integer.parseInt(inputData[0]);
                sTemp = inputData[1];
                locations.put(iTemp,new Location(iTemp, sTemp) );
                iTemp=-1;
                sTemp=null;
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
            System.out.println("Failed to open / create a file handle");
        }

        String direction, inputExit;
        Integer inode=0, newNode=0;

        try(BufferedReader brExit = new BufferedReader(new FileReader(DIRECTIONMAP)))
        {
            while((inputExit = brExit.readLine()) != null)
            {
                String inputExitData[] = inputExit.split(",");
                inode = Integer.parseInt(inputExitData[0]);
                direction = inputExitData[1];
                newNode = Integer.parseInt(inputExitData[2]);

                if(!direction.equalsIgnoreCase("Q"))
                {
                    locations.get(inode).addExit(direction, newNode);
                }
              }
            locations.get(inode).addExit("Q", 0);
            inode=-1;
            newNode = -1;
            direction=null;
        }
        catch(IOException e)
        {
            e.printStackTrace();
            System.out.println("Failed to open / create a file handle");
        }
        for(Location loc:locations.values())
        {
            loc.addExit("Q", 0);
            System.out.println(loc.toString());
        }
    }

    // DELEGATE the implemented methods to the HashMap object "locations".
    @Override
    public
    int size()
    {
        return locations.size();
    }

    @Override
    public
    boolean isEmpty()
    {
        return locations.isEmpty();
    }

    @Override
    public
    boolean containsKey(Object key)
    {
        return locations.containsKey(key);
    }

    @Override
    public
    boolean containsValue(Object value)
    {
        return locations.containsValue(value);
    }

    @Override
    public
    Location get(Object key)
    {
        return locations.get(key);
    }

    @Override
    public
    Location put(Integer key, Location value)
    {
        return locations.put(key,value);
    }

    @Override
    public
    Location remove(Object key)
    {
        return locations.remove(key);
    }

    @Override
    public
    void putAll(Map<? extends Integer, ? extends Location> m)
    {
//        locations.putAll(m);
    }

    @Override
    public
    void clear()
    {
        locations.clear();
    }

    @Override
    public
    Set<Integer> keySet()
    {
        return locations.keySet();
    }

    @Override
    public
    Collection<Location> values()
    {
        return locations.values();
    }

    @Override
    public
    Set<Entry<Integer, Location>> entrySet()
    {
        return locations.entrySet();
    }

}
