package net.alcyon;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.URI;
import java.util.Properties;

import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.protocol.RequestExpectContinue;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.impl.DefaultBHttpClientConnection;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.protocol.HttpCoreContext;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpProcessorBuilder;
import org.apache.http.protocol.HttpRequestExecutor;
import org.apache.http.protocol.RequestConnControl;
import org.apache.http.protocol.RequestContent;
import org.apache.http.protocol.RequestTargetHost;
import org.apache.http.protocol.RequestUserAgent;
import org.apache.http.util.EntityUtils;

import com.netflix.exhibitor.core.config.ConfigCollection;
import com.netflix.exhibitor.core.config.ConfigProvider;
import com.netflix.exhibitor.core.config.LoadedInstanceConfig;
import com.netflix.exhibitor.core.config.PropertyBasedInstanceConfig;
import com.netflix.exhibitor.core.config.PseudoLock;

public class EtcdConfigProvider implements ConfigProvider {
  private static final String     CONFIG_NODE_NAME = "config";
  private static final String     LOCK_PATH = "locks";
  private static final String     CONFIG_PATH = "configs";
  
  private String baseUri;
  private String basePath;
  private Properties defaults;
  private String hostname;
  

  public EtcdConfigProvider(String baseUri, String basePath, Properties defaults, String hostname) {
    this.baseUri = baseUri;
    this.basePath = basePath;
    this.defaults = defaults;
    this.hostname = hostname;
  }
  
  @Override
  public void close() throws IOException {
    
  }

  @Override
  public void start() throws Exception {
    
  }

  private InputStream fetch(String uri) throws HttpException, IOException {
    HttpProcessor httpproc = HttpProcessorBuilder.create()
        .add(new RequestContent())
        .add(new RequestTargetHost())
        .add(new RequestConnControl())
        .add(new RequestUserAgent("Exhibitor-EtcdConfigProvider/1.0"))
        .add(new RequestExpectContinue()).build();

    HttpRequestExecutor httpexecutor = new HttpRequestExecutor();

    HttpCoreContext coreContext = HttpCoreContext.create();
    URI parsedUri = URI.create(uri);
    HttpHost host = URIUtils.extractHost(parsedUri);
    
    coreContext.setTargetHost(host);

    DefaultBHttpClientConnection conn = null;
    try {
      conn = new DefaultBHttpClientConnection(8 * 1024);
      Socket socket = new Socket(host.getHostName(), host.getPort());
      conn.bind(socket);
      
     //ConnectionReuseStrategy connStrategy = DefaultConnectionReuseStrategy.INSTANCE;
    
	    String u = parsedUri.getRawPath() + 
	               (parsedUri.getRawQuery() == null ? "" : "?" + parsedUri.getRawQuery()) +
	               (parsedUri.getRawFragment() == null ? "" : "#" + parsedUri.getRawFragment());
	    
	    BasicHttpRequest request = new BasicHttpRequest("GET", u);
	    
	    httpexecutor.preProcess(request, httpproc, coreContext);
	    HttpResponse response = httpexecutor.execute(request, conn, coreContext);
	    httpexecutor.postProcess(response, httpproc, coreContext);
	    
	    return new ByteArrayInputStream(EntityUtils.toByteArray(response.getEntity()));
    } finally {
	    if( conn != null ){
	      conn.close();
	    }
    }
    
  }
  
  @Override
  public LoadedInstanceConfig loadConfig() throws Exception {
    String url = baseUri+basePath+"/"+CONFIG_PATH+"/"+CONFIG_NODE_NAME;
    
    Properties props = new Properties(defaults);
    props.load(fetch(url));

    version = childData.getStat().getVersion();
    properties.load(new ByteArrayInputStream(childData.getData()));
}
PropertyBasedInstanceConfig config = new PropertyBasedInstanceConfig(properties, defaults);
return new LoadedInstanceConfig(config, version);
    return null;
  }

  @Override
  public LoadedInstanceConfig storeConfig(ConfigCollection config, long compareVersion) throws Exception {
    return null;
  }

  @Override
  public PseudoLock newPseudoLock() throws Exception {
    return null;
  }
  
  public String getBasePath() {
    return basePath;
  }

  public void setBasePath(String basePath) {
    this.basePath = basePath;
  }

  public Properties getDefaults() {
    return defaults;
  }

  public void setDefaults(Properties defaults) {
    this.defaults = defaults;
  }

  public String getHostname() {
    return hostname;
  }

  public void setHostname(String hostname) {
    this.hostname = hostname;
  }

}
