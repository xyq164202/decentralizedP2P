package helper;

public class NetAddress {
    private String address;
    private int port;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public String toString() {
        return "NetAddress{" +
                "address='" + address + '\'' +
                ", port=" + port +
                '}';
    }
}
