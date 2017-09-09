import java.io.IOException;

/**
 * Created by ramya on 9/4/17.
 */
public class RecordFormat {

    public static String code;

    public static int getRecordFormat(String code) {
        int length = 0;
        switch (code) {
            case "null1":
                length = 1;
                break;
            case "null2":
                length = 2;
                break;
            case "null3":
                length = 4;
                break;
            case "null4":
                length = 8;
                break;
            case "tinyint":
                length = 1;
                break;
            case "smallint":
                length = 2;
                break;
            case "int":
                length = 4;
                break;
            case "bigint":
                length = 8;
                break;
            case "real":
                length = 4;
                break;
            case "double":
                length = 8;
                break;
            case "datetime":
                length = 8;
                break;
            case "date":
                length = 8;
                break;
            case "text":
                length = 24;
                break;
            case "shorttext":
                length = 5;
                break;


            default:
                System.out.println("Please enter a valid type");
                break;
        }

        return length;
    }

    public static long writeRecordFormat(String dataType, Table table, long pos, String record) throws IOException {

//        System.out.println("Calling Write Record with Parameters : ");
//        System.out.println("Code : "+code);
//        System.out.println("pos : "+pos);
//        System.out.println("record : "+record);

        switch (dataType) {
            case "1":
                pos = table.page.writeNullasByte("N", pos, 1);
                break;
            case "2":
                pos = table.page.writeNullasByte("N", pos, 2);
                break;
            case "4":
                pos = table.page.writeNullasByte("N", pos, 4);
                break;
            case "8":
                pos = table.page.writeNullasByte("N", pos, 8);
                break;
            case "tinyint":
                pos = table.page.writeTinyIntasByte(record.getBytes(), pos);
                break;
            case "smallint":

                pos = table.page.writeShortasByte(Integer.parseInt(record), pos);
                break;
            case "int":
                pos = table.page.writeIntasByte(Integer.parseInt(record), pos);
                break;
            case "bigint":
                pos = table.page.writeLongasByte(Long.parseLong(record), pos);
                break;
            //TODO: add LATER
            case "real":
//                length = 4;
                break;
            case "double":
//                length = 8;
                break;
            case "datetime":
//                length = 8;
                break;
            case "date":
//                length = 8;
                break;
            case "text":
                pos = table.page.writeStringasByte(record, pos);
                break;
            case "shorttext":
                pos = table.page.writeShortStringasByte(record, pos);
//                return pos;
                break;


            default:
                System.out.println("Please enter a valid type");
                break;
        }
        return pos;

    }    public static ReadResult<Object> readRecordFormat(String dataType, Table table, long pos) throws IOException {

        ReadResult<Object> readResult = null;

        switch (dataType) {
            case "1":
//                pos = table.page.writeNullasByte("N", pos, 1);
                break;
            case "2":
//                pos = table.page.writeNullasByte("N", pos, 2);
                break;
            case "4":
//                pos = table.page.writeNullasByte("N", pos, 4);
                break;
            case "8":
//                pos = table.page.writeNullasByte("N", pos, 8);
                break;
            case "tinyint":
                readResult = table.page.readTinyIntAsByte(pos);
                break;
            case "smallint":
                readResult = table.page.readShortAsByte(pos);
                break;
            case "int":
                readResult = table.page.readIntasByte(pos);
                break;
            case "bigint":
                readResult = table.page.readLongAsByte(pos);
                break;
            //TODO: add LATER
            case "real":
//                length = 4;
                break;
            case "double":
//                length = 8;
                break;
            case "datetime":
//                length = 8;
                break;
            case "date":
//                length = 8;
                break;
            case "text":
                readResult = table.page.readStringAsByte(pos);
                break;
            case "shorttext":
                readResult = table.page.readShortStringAsByte(pos);

                break;


            default:
                readResult = new ReadResult<Object>(pos,"PLEASE ENTER A VALID VALUE");
                break;
        }

        return readResult;
    }
}

     class ReadResult<Type> {
        long pos;
        Type t;

        public long getPos() {
            return pos;
        }

        public Type getT() {
            return t;
        }

        public ReadResult(long pos, Type t) {
            this.pos = pos;
            this.t = t;


        }
//
//    private RecordFormat(String code)
//    {
//        this.code=code;
//    }
//
//    public String getCode(){
//        return code;
//    }
//
//    0x00 NULL 1 Value is a 1-byte NULL (used for NULL TINYINT)
//0x01 NULL 2 Value is a 2-byte NULL (used for NULL SMALLINT)
//0x02 NULL 4 Value is a 4-byte NULL (used for NULL INT or REAL)
//0x03 NULL 8 Value is a 8-byte NULL (used for NULL DOUBLE, DATETIME, or DATE
//0x04 TINYINT 1 Value is a big-endian 1-byte twos-complement integer.
//        0x05 SMALLINT 2 Value is a big-endian 2-byte twos-complement integer.
//        0x06 INT 4 Value is a big-endian 4-byte twos-complement integer.
//        0x07 BIGINT 8 Value is an big-endian 8-byte twos-complement integer.
//        0x08 REAL 4 A big-endian single precision IEEE 754 floating point number
//        0x09 DOUBLE 8 A big-endian double precision IEEE 754 floating point number
//        0x0A DATETIME 8
//        A big-endian unsigned LONG integer that represents the specified
//        number of milliseconds since the standard base time known as "the
//        epoch”. It should display as a formatted string string:
//        YYYY-MM-DD_hh:mm:ss, e.g. 2016-03-23_13:52:23.
//                                            0x0B DATE 8 A datetime whose time component is 00:00:00, but does not display.
//                                            0x0C + n TEXT
//                                            Value is a string in ASCI encoding (range 0x00-0x7F) of length n. For
//    the purposes of this database you may consider that the empty string
//    is a NULL value, i.e. empty strings do not exist. The null terminator is
//    not stored.


    }

