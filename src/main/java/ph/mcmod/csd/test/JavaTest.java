package ph.mcmod.csd.test;

import java.net.MalformedURLException;
import java.net.URL;
/**
 * @version 0.1
 */
public class JavaTest {
    private URL url;

    public void setUrl(URL url) {
        this.url = url;
    }

    public void setUrl(String url) throws MalformedURLException {
        setUrl(new URL(url));
    }

    public URL getUrl() {
        return url;
    }
}