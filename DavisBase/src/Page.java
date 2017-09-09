import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

/**
 * Created by ramya on 7/4/17.
 */
public class Page extends RandomAccessFile {

    Page page = null;



    public Page(String name) throws FileNotFoundException {
            super(name, "rw");

    }

    public void populatePageNewHeaders(Page pg,int leafNode ){
        try {
            pg.setLength(Constant.pageSize);

            pg.seek(0);
            if(leafNode == 1)
                pg.writeByte((Constant.leafNode));
            else
                pg.writeByte(Constant.innerNode);

            pg.seek(0 + Constant.noOfRecordsOffset);
            pg.writeByte(Constant.newPageNoOfRecords);

            pg.seek(0 + Constant.startOfContentOffset);
            ByteBuffer b =  ByteBuffer.allocate(Constant.smallSize);
            b.putShort((short) Constant.newPageOffset);
            pg.write(b.array());

            b = ByteBuffer.allocate(Constant.intSize);
            pg.seek(0+Constant.rightPageOffset);
            b.putInt(2* Integer.MAX_VALUE + 1);
            pg.write(b.array());

//            pg.seek(0+Constant.arrayOfRecordsOffset);
//            b =  ByteBuffer.allocate(Constant.smallSize);
//            b.putShort((short) 485);
//            pg.write(b.array());


        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public void updateNoOfRecInPageHeader() throws IOException {
        int noOfRecords = getNoOfRecords();
        noOfRecords = noOfRecords+1;
        seek(Constant.noOfRecordsOffset);
        writeByte(noOfRecords);
    }
    public int getNoOfRecords() throws IOException {
        seek(Constant.noOfRecordsOffset);
        int noOfRecords = read();
        return noOfRecords;
    }
    public void updateArrOfRecLocInPageHeader(Short value) throws IOException {
        int noOfRecords = getNoOfRecords();
        seek(Constant.arrayOfRecordsOffset + noOfRecords*Constant.smallSize);
        writeShortasByte(value,getFilePointer());
    }
    //TODO: Binary search Implemenatation
    public short updateStartOfContent(short value) throws IOException {
        seek(Constant.startOfContentOffset);
        writeShortasByte(value,getFilePointer());
        return value;
    }
    public long writeIntasByte(Integer value, long pos) throws IOException {
        seek(pos);
        ByteBuffer b =  ByteBuffer.allocate(Constant.intSize);
        b.putInt(value.intValue());
        write(b.array());
        pos = pos + RecordFormat.getRecordFormat("int");
        return pos;
    }
    public long writeLongasByte(Long value, long pos) throws IOException {
        seek(pos);
        ByteBuffer b =  ByteBuffer.allocate(Constant.longSize);
        b.putLong(value.longValue());
        write(b.array());
        pos = pos + RecordFormat.getRecordFormat("long");
        return pos;
    }

    public long writeShortasByte(short value, long pos) throws IOException {
        seek(pos);
        ByteBuffer b =  ByteBuffer.allocate(Constant.smallSize);
        b.putShort(value);
        write(b.array());
        pos = pos+RecordFormat.getRecordFormat("smallint");
        return pos;
    }
    public long writeShortasByte(Integer value, long pos) throws IOException {
        seek(pos);
        ByteBuffer b =  ByteBuffer.allocate(Constant.smallSize);
        b.putShort(value.shortValue());
        write(b.array());
        pos = pos+RecordFormat.getRecordFormat("smallint");
        return pos;
    }
    public long writeStringasByte(String value, long pos) throws IOException {
        seek(pos);
        writeBytes(value);
        pos = pos+RecordFormat.getRecordFormat("text");
        return pos;
    }
    public long writeNullasByte(String value, long pos, int lengthOfBytes) throws IOException {
        seek(pos);
        writeBytes(value);
        pos = pos+lengthOfBytes;
        return pos;
    }
    public long writeShortStringasByte(String value, long pos) throws IOException {
        seek(pos);
        writeBytes(value);
        pos = pos+RecordFormat.getRecordFormat("shorttext");
        return pos;
    }
    public long writeTinyIntasByte(byte b[], long pos) throws IOException {
        seek(pos);
        write(b);
        pos = pos+RecordFormat.getRecordFormat("tinyint");
        return pos;
    }
    public ReadResult readIntasByte(long pos) throws IOException {
        seek(pos);
        byte[] b = new byte[RecordFormat.getRecordFormat("int")];
        readFully(b);
        ByteBuffer buffer = ByteBuffer.allocate(RecordFormat.getRecordFormat("int"));
        buffer.put(b);
        buffer.rewind();
        int val = buffer.getInt();
        pos = pos + RecordFormat.getRecordFormat("int");
        return new ReadResult<Integer>(pos,val);
    }
    public short readShortByte(long pos) throws IOException {
        seek(pos);
        byte[] b = new byte[Constant.smallSize];
        readFully(b);
        ByteBuffer buffer = ByteBuffer.allocate(Constant.smallSize);
        buffer.put(b);
        buffer.flip();
//        buffer.rewind();
        short val = buffer.getShort();
//        pos = pos + RecordFormat.getRecordFormat("smallint");
        return val;
    }
    public ReadResult readShortAsByte(long pos) throws IOException {
        seek(pos);
        byte[] b = new byte[Constant.smallSize];
        readFully(b);
        ByteBuffer buffer = ByteBuffer.allocate(Constant.smallSize);
        buffer.put(b);
        buffer.flip();
//        buffer.rewind();
        short val = buffer.getShort();
        pos = pos + RecordFormat.getRecordFormat("smallint");
        return new ReadResult<Integer>(pos,(int) val);
    }
    public ReadResult readLongAsByte(long pos) throws IOException {
        seek(pos);
        byte[] b = new byte[RecordFormat.getRecordFormat("long")];
        readFully(b);
        ByteBuffer buffer = ByteBuffer.allocate(RecordFormat.getRecordFormat("long"));
        buffer.put(b);
        buffer.rewind();
        long val = buffer.getLong();
        pos = pos + RecordFormat.getRecordFormat("smallint");
        return new ReadResult<Long>(pos, val);
    }
    public ReadResult readTinyIntAsByte(long pos) throws IOException {
        seek(pos);
        int val = read();
        pos = pos + RecordFormat.getRecordFormat("tinyint");
        return new ReadResult<Integer>(pos,val);
    }
    public ReadResult readStringAsByte(long pos) throws IOException {
        seek(pos);

        byte b[] = new byte[RecordFormat.getRecordFormat("text")];
        read(b);
        ByteBuffer buffer = ByteBuffer.allocate(RecordFormat.getRecordFormat("text"));
        buffer.put(b);
        buffer.rewind();
        String val = new String(b);
        val = val.replaceAll("[\u0000-\u001f]", "");
        pos = pos+ RecordFormat.getRecordFormat("text");
        return new ReadResult<String>(pos,val);

    }
    public ReadResult readShortStringAsByte(long pos) throws IOException {
        seek(pos);

        byte b[] = new byte[RecordFormat.getRecordFormat("shorttext")];
        read(b);
        ByteBuffer buffer = ByteBuffer.allocate(RecordFormat.getRecordFormat("shorttext"));
        buffer.put(b);
        buffer.rewind();
        String val = new String(b);
        val = val.replaceAll("[\u0000-\u001f]", "");
        pos = pos+ RecordFormat.getRecordFormat("shorttext");
        return new ReadResult<String>(pos,val);

    }



    public long getStartofContent(){
        long startofContent = 0;
        try {

            seek(Constant.startOfContentOffset);
            startofContent = this.readShortByte(Constant.startOfContentOffset);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return startofContent;

    }

    public long getFirstAvailableFreeByte(){
        long firstAvailableFreeByte = 0;
        try {
            seek(Constant.pageHeaderConstantSize + getNoOfRecords()*Constant.smallSize);
            firstAvailableFreeByte = getFilePointer();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return firstAvailableFreeByte;
    }


}

