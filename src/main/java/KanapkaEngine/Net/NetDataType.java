package KanapkaEngine.Net;

public enum NetDataType {
    FLOAT(4), DOUBLE(8), INT(4), SHORT(2), BYTE(1), CHAR(2);

    public final int size;

    NetDataType(int size) {
        this.size = size;
    }
}
