package cn.InstFS.wkr.NetworkMining.PcapStatisticsDis;

public class PcapHeader {
    private int magic;//文件识别头,为0xA1B2C3D4
    private short major_version;//主要版本
    private short minor_version;//次要版本
    private int timezone;//当地标准时间
    private int sigflags;//时间戳的精度
    private int snaplen;//最大的存储长度
    /**
     * 0            BSD loopback devices, except for later OpenBSD
     * 1            Ethernet, and Linux loopback devices
     * 6            802.5 Token Ring
     * 7            ARCnet
     * 8            SLIP
     * 9            PPP
     * 10           FDDI
     * 100         LLC/SNAP-encapsulated ATM
     * 101         “raw IP”, with no link
     * 102         BSD/OS SLIP
     * 103         BSD/OS PPP
     * 104         Cisco HDLC
     * 105         802.11
     * 108         later OpenBSD loopback devices (with the AF_value in network byte order)
     * 113         special Linux “cooked” capture
     * 114         LocalTalk
     */
    private int linktype;//链路类型

    public int getMagic() {
        return magic;
    }

    public void setMagic(int magic) {
        this.magic = magic;
    }

    public short getMajor_version() {
        return major_version;
    }

    public void setMajor_version(short magor_version) {
        this.major_version = magor_version;
    }

    public short getMinor_version() {
        return minor_version;
    }

    public void setMinor_version(short minor_version) {
        this.minor_version = minor_version;
    }

    public int getTimezone() {
        return timezone;
    }

    public void setTimezone(int timezone) {
        this.timezone = timezone;
    }

    public int getSigflags() {
        return sigflags;
    }

    public void setSigflags(int sigflags) {
        this.sigflags = sigflags;
    }

    public int getSnaplen() {
        return snaplen;
    }

    public void setSnaplen(int snaplen) {
        this.snaplen = snaplen;
    }

    public int getLinktype() {
        return linktype;
    }

    public void setLinktype(int linktype) {
        this.linktype = linktype;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("magic=").append("0x" + Integer.toHexString(this.magic));
        s.append("\nmagor_version=").append(this.major_version);
        s.append("\nminor_version=").append(this.minor_version);
        s.append("\ntimezone=").append(this.timezone);
        s.append("\nsigflags=").append(this.sigflags);
        s.append("\nsnaplen=").append(this.snaplen);
        s.append("\nlinktype=").append(this.linktype);
        return s.toString();
    }
}
