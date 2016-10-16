
package tikape.runko.domain;

public class Sivu {
    private String url;
    private boolean current;
    private int number;

    public Sivu(String url, boolean current, int number) {
        this.url = url;
        this.current = current;
        this.number = number;
    }

    public String getUrl() {
        return url;
    }

    public boolean isCurrent() {
        return current;
    }

    public int getNumber() {
        return number;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setCurrent(boolean current) {
        this.current = current;
    }

    public void setNumber(int number) {
        this.number = number;
    }
    
}
