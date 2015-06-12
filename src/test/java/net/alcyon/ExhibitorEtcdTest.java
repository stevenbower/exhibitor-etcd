package net.alcyon;

import java.util.Properties;

import org.junit.Test;

import com.netflix.exhibitor.core.config.ConfigProvider;

public class ExhibitorEtcdTest {

  @Test
  public void test() throws Exception {
    
    //BackupProvider backupProvider;
    ConfigProvider cfgProvider = new EtcdConfigProvider("http://127.0.0.1:4001/version", "/exhibitor", new Properties(), "test-host");
    
    
    //ExhibitorMain exhibitorMain = new ExhibitorMain(null, null, null, 0, null, null);
  }
}
