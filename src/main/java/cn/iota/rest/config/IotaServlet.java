package cn.iota.rest.config;

public class IotaServlet {

    /**
     * The context path for the rest servlet.
     */
    private String path;

    /**
     * The name of the servlet.
     */
    private String name;

    /**
     * Load on startup of the dispatcher servlet
     */
    private int loadOnStartup = -1;

    public IotaServlet(String path, String name) {
        this.path = path;
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLoadOnStartup() {
        return loadOnStartup;
    }

    public void setLoadOnStartup(int loadOnStartup) {
        this.loadOnStartup = loadOnStartup;
    }

}
