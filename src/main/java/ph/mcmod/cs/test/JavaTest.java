package ph.mcmod.cs.test;

import kotlin.Unit;
import ph.mcmod.cs.Main;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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