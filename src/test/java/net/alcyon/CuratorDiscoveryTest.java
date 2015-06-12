package net.alcyon;

import org.apache.curator.test.TestingServer;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;
import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

public class CuratorDiscoveryTest {

  //@JsonSerialize(using=FooSerializer.class)

  @JsonTypeInfo(use=Id.CLASS, include=As.PROPERTY)
	public static class Foo {
		private int foo = 23;

    public int getFoo() {
      return foo;
    }

    public void setFoo(int foo) {
      this.foo = foo;
    }
	}
	
	@Test
	public void test() throws Exception {
		ServiceDiscovery<Foo> discovery = null;
		try (TestingServer server = new TestingServer()){
			//CuratorFramework client = CuratorFrameworkFactory.builder().connectString(server.getConnectString()).retryPolicy(new RetryNTimes(0,0)).build();
			//client.start();
			ServiceInstance<Foo> svc1 = ServiceInstance.<Foo>builder().name("service1").id("instance1").build();
			ServiceInstance<Foo> svc2 = ServiceInstance.<Foo>builder().name("service1").id("instance2").payload(new Foo()).build();
			//discovery = ServiceDiscoveryBuilder.builder(Foo.class).basePath("/service_discovery").client(client).thisInstance(svc1).build();
			//discovery.start();
			

			JsonInstanceSerializer<Foo> serializer = new JsonInstanceSerializer<Foo>(Foo.class);
			String out;
			
			out = new String(serializer.serialize(svc1));
			System.out.println(out);
			
			out = new String(serializer.serialize(svc2));
			System.out.println(out);
			
		} finally {
			if(discovery != null){
				discovery.close();
			}
		}
	}
		
		
}
