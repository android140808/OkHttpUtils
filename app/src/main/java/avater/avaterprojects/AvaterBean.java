package avater.avaterprojects;

/**
 * 作者:Avater
 * 日期： 2017-09-03.
 * 说明：
 */

public class AvaterBean {
    private String x;
    private String y;

    public String getX() {
        return x;
    }

    public void setX(String x) {
        this.x = x;
    }

    @Override
    public String toString() {
        return "AvaterBean{" +
                "x='" + x + '\'' +
                ", y='" + y + '\'' +
                '}';
    }
}
